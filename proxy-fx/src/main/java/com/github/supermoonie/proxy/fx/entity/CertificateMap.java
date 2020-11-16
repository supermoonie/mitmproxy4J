package com.github.supermoonie.proxy.fx.entity;

/**
 * @author supermoonie
 * @date 2020-11-16
 */
public class CertificateMap extends BaseEntity {

    private String certificateSerialNumber;
    private String requestId;
    private String responseId;

    public String getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    public void setCertificateSerialNumber(String certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    @Override
    public String toString() {
        return "CertificateMap{" +
                "certificateSerialNumber='" + certificateSerialNumber + '\'' +
                ", requestId='" + requestId + '\'' +
                ", responseId='" + responseId + '\'' +
                '}';
    }
}
