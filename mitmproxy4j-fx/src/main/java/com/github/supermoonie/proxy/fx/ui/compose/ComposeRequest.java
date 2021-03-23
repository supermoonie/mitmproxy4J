package com.github.supermoonie.proxy.fx.ui.compose;

import com.github.supermoonie.proxy.fx.ui.KeyValue;

import java.util.List;

/**
 * @author supermoonie
 * @since 2021/3/23
 */
public class ComposeRequest {

    private String method;

    private String url;

    private List<KeyValue> headerList;

    private String mimeType;

    private List<FormData> formDataList;

    private List<KeyValue> formUrlencodedList;

    private String binary;

    private String raw;

    private String rawType;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<KeyValue> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<KeyValue> headerList) {
        this.headerList = headerList;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List<FormData> getFormDataList() {
        return formDataList;
    }

    public void setFormDataList(List<FormData> formDataList) {
        this.formDataList = formDataList;
    }

    public List<KeyValue> getFormUrlencodedList() {
        return formUrlencodedList;
    }

    public void setFormUrlencodedList(List<KeyValue> formUrlencodedList) {
        this.formUrlencodedList = formUrlencodedList;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getRawType() {
        return rawType;
    }

    public void setRawType(String rawType) {
        this.rawType = rawType;
    }
}
