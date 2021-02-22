package com.github.supermoonie.proxy.fx.controller;

import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;

import java.util.List;

/**
 * @author supermoonie
 * @since 2021/2/22
 */
public class Flow {

    private List<Header> requestHeaders;

    private List<Header> responseHeaders;

    private Request request;

    private Response response;

    private String hexRequestContent;

    private String hexResponseContent;

    public List<Header> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(List<Header> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public List<Header> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(List<Header> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getHexRequestContent() {
        return hexRequestContent;
    }

    public void setHexRequestContent(String hexRequestContent) {
        this.hexRequestContent = hexRequestContent;
    }

    public String getHexResponseContent() {
        return hexResponseContent;
    }

    public void setHexResponseContent(String hexResponseContent) {
        this.hexResponseContent = hexResponseContent;
    }
}
