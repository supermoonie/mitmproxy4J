package com.github.supermoonie.util;

import com.github.supermoonie.collection.LinkedMultiObjectValueMap;
import com.github.supermoonie.controller.vo.HttpRequestVO;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/6/25
 */
public final class RequestUtils {

    private RequestUtils() {}

    public static HttpRequestVO format(HttpServletRequest request) {
        HttpRequestVO vo = new HttpRequestVO();
        vo.setBody(readBody(request));
        Map<String, String[]> parameterMap = request.getParameterMap();
        LinkedMultiObjectValueMap<String> form = new LinkedMultiObjectValueMap<>(parameterMap.size());
        form.addAll(parameterMap);
        vo.setForm(form);
        String queryString = request.getQueryString();
        StringBuffer url = request.getRequestURL();
        vo.setArgs(parseQueryString(queryString));
        vo.setHeaders(getHeaders(request));
        vo.setUrl(url.toString());
        return vo;
    }

    public static String readBody(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()){
            String line;
            StringBuilder res = new StringBuilder();
            while((line = reader.readLine()) != null) {
                res.append(line);
            }
            return res.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static LinkedMultiObjectValueMap<String> parseQueryString(String queryString) {
        if (StringUtils.isEmpty(queryString)) {
            return new LinkedMultiObjectValueMap<>(0);
        }
        LinkedMultiObjectValueMap<String> map = new LinkedMultiObjectValueMap<>(5);
        String[] queryArr = queryString.split("&");
        for (String query : queryArr) {
            String[] param = query.split("=");
            map.add(param[0], param[1]);
        }
        return map;
    }

    public static LinkedMultiObjectValueMap<String> getHeaders(HttpServletRequest request) {
        LinkedMultiObjectValueMap<String> map = new LinkedMultiObjectValueMap<>(5);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                map.add(name, value);
            }
        }
        return map;
    }
}
