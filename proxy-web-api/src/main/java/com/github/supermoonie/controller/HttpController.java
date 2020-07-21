package com.github.supermoonie.controller;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.supermoonie.config.MyProxyConfig;
import com.github.supermoonie.constant.EnumBodyType;
import com.github.supermoonie.constant.EnumTextFormDataType;
import com.github.supermoonie.controller.params.TextFormData;
import com.github.supermoonie.exception.ApiException;
import com.github.supermoonie.message.SimpleHeader;
import com.github.supermoonie.service.HttpService;
import com.github.supermoonie.util.HttpClientUtils;
import com.github.supermoonie.util.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/7/12
 */
@Api
@RestController
@RequestMapping(value = "/http")
@Slf4j
@CrossOrigin
public class HttpController {

    @Resource
    private MyProxyConfig myProxyConfig;

    @Resource
    private HttpService httpService;

    @ApiOperation(value = "发请求", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @PostMapping(value = "/execute", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> execute(@RequestParam(value = "method") String method,
                                          @RequestParam("url") String url,
                                          @RequestParam("headers") String headers,
                                          @RequestParam(value = "requestContentType", required = false) String requestContentType,
                                          @RequestParam(value = "textFormData", required = false) String textFormData,
                                          HttpServletRequest request) {
        HttpMethod httpMethod = resolveHttpMethod(method);
        URI uri = resolveUrl(url);
        List<String> notAddHeaders = Arrays.asList("content-length", "content-type", "host");
        List<BasicHeader> basicHeaders = resolveHeaders(headers).stream().filter(header -> !notAddHeaders.contains(header.getName().toLowerCase())).collect(Collectors.toList());

        HttpClientBuilder httpClientBuilder = HttpClientUtils.createTrustAllHttpClientBuilder();
        httpClientBuilder.setProxy(new HttpHost("127.0.0.1", myProxyConfig.getPort()));
        try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
            RequestBuilder requestBuilder = RequestBuilder.create(httpMethod.name()).setUri(uri);
            basicHeaders.forEach(requestBuilder::setHeader);
            switch (httpMethod) {
                case POST:
                case PUT:
                case PATCH:
                    EnumBodyType bodyType = EnumBodyType.of(requestContentType).orElseThrow(() -> new ApiException("Request contentType: " + requestContentType + " not support!", HttpStatus.BAD_GATEWAY));
                    List<TextFormData> textFormDataList = resolveTextFormData(textFormData);
                    switch (bodyType) {
                        case FORM_DATA:
                            List<TextFormData> hexFileFormDataList = textFormDataList.stream().filter(data -> null != data.getType() && data.getType().equals(EnumTextFormDataType.FILE.getType())).collect(Collectors.toList());
                            textFormDataList = textFormDataList.stream().filter(data -> null != data.getType() && data.getType().equals(EnumTextFormDataType.TEXT.getType())).collect(Collectors.toList());
                            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                            textFormDataList.forEach(data -> entityBuilder.addTextBody(data.getName(),
                                    data.getValue(), ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), "UTF-8")));
                            hexFileFormDataList.forEach(hexFileFormData -> entityBuilder.addBinaryBody(hexFileFormData.getName(),
                                    HexUtil.decodeHex(hexFileFormData.getValue()),
                                    ContentType.parse(hexFileFormData.getContentType()),
                                    hexFileFormData.getFileName()));
                            Collection<Part> parts = request.getParts();
                            for (Part part : parts) {
                                String contentDisposition = part.getHeader("Content-Disposition");
                                if (!contentDisposition.matches("form-data;\\s+name=.*;\\s+filename=.*")) {
                                    continue;
                                }
                                String[] dispositionArr = contentDisposition.split(";");
                                String name = dispositionArr[1].substring(dispositionArr[1].indexOf("\"") + 1, dispositionArr[1].lastIndexOf("\""));
                                String fileName = dispositionArr[2].substring(dispositionArr[2].indexOf("\"") + 1, dispositionArr[2].lastIndexOf("\""));
                                byte[] bytes = IOUtils.readFully(part.getInputStream(), (int) part.getSize());
                                entityBuilder.addBinaryBody(name, bytes, ContentType.create(part.getContentType()), fileName);
                            }
                            HttpEntity httpEntity = entityBuilder.build();
                            requestBuilder.setEntity(httpEntity);
                            break;
                        case X_WWW_FORM_URLENCODED:
                            textFormDataList.forEach(data -> requestBuilder.addParameter(data.getName(), data.getValue()));
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            httpClient.execute(requestBuilder.build(), response -> null);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RuntimeException | ServletException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("OK");
    }

    private List<TextFormData> resolveTextFormData(String textFormData) {
        List<TextFormData> textFormDataList;
        if (!StringUtils.isEmpty(textFormData)) {
            try {
                textFormDataList = JSON.parse(textFormData, new TypeReference<List<TextFormData>>() {
                });
            } catch (RuntimeException e) {
                throw new ApiException(e.getMessage(), e, HttpStatus.BAD_GATEWAY);
            }
        } else {
            textFormDataList = new ArrayList<>();
        }
        return textFormDataList;
    }

    private List<BasicHeader> resolveHeaders(String headers) {
        List<BasicHeader> basicHeaders;
        if (!StringUtils.isEmpty(headers)) {
            try {
                List<SimpleHeader> simpleHeaders = JSON.parse(headers, new TypeReference<List<SimpleHeader>>() {
                });
                basicHeaders = simpleHeaders.stream().map(header -> new BasicHeader(header.getName(), header.getValue())).collect(Collectors.toList());
            } catch (RuntimeException e) {
                throw new ApiException(e.getMessage(), e, HttpStatus.BAD_GATEWAY);
            }
        } else {
            basicHeaders = new ArrayList<>();
        }
        return basicHeaders;
    }

    private URI resolveUrl(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new ApiException(e.getMessage(), e, HttpStatus.BAD_GATEWAY);
        }
    }

    private HttpMethod resolveHttpMethod(String method) {
        if (StringUtils.isEmpty(method)) {
            throw new ApiException("Method lost!", HttpStatus.BAD_GATEWAY);
        }
        method = method.toUpperCase();
        HttpMethod httpMethod = HttpMethod.resolve(method);
        if (null == httpMethod) {
            throw new ApiException("Method: " + method + " not support!", HttpStatus.BAD_GATEWAY);
        }
        return httpMethod;
    }
}
