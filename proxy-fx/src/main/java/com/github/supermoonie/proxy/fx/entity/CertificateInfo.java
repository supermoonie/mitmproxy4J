package com.github.supermoonie.proxy.fx.entity;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
public class CertificateInfo extends BaseEntity {

    private String requestId;
    private String responseId;
    private String issuerCommonName;
    private String issuerOrganizationDepartment;
    private String issuerOrganizationName;
    private String issuerLocalityName;
    private String issuerStateName;
    private String issuerCountry;
    private String subjectCommonName;
    private String subjectOrganizationDepartment;
    private String subjectOrganizationName;
    private String subjectLocalityName;
    private String subjectStateName;
    private String subjectCountry;
    private String type;
    private Integer version;
    private String sigAlgName;
    private Date notValidBefore;
    private Date notValidAfter;
    private String shaOne;
    private String shaTwoFiveSix;
    private String fullDetail;

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

    public String getIssuerCommonName() {
        return issuerCommonName;
    }

    public void setIssuerCommonName(String issuerCommonName) {
        this.issuerCommonName = issuerCommonName;
    }

    public String getIssuerOrganizationDepartment() {
        return issuerOrganizationDepartment;
    }

    public void setIssuerOrganizationDepartment(String issuerOrganizationDepartment) {
        this.issuerOrganizationDepartment = issuerOrganizationDepartment;
    }

    public String getIssuerOrganizationName() {
        return issuerOrganizationName;
    }

    public void setIssuerOrganizationName(String issuerOrganizationName) {
        this.issuerOrganizationName = issuerOrganizationName;
    }

    public String getIssuerLocalityName() {
        return issuerLocalityName;
    }

    public void setIssuerLocalityName(String issuerLocalityName) {
        this.issuerLocalityName = issuerLocalityName;
    }

    public String getIssuerStateName() {
        return issuerStateName;
    }

    public void setIssuerStateName(String issuerStateName) {
        this.issuerStateName = issuerStateName;
    }

    public String getIssuerCountry() {
        return issuerCountry;
    }

    public void setIssuerCountry(String issuerCountry) {
        this.issuerCountry = issuerCountry;
    }

    public String getSubjectCommonName() {
        return subjectCommonName;
    }

    public void setSubjectCommonName(String subjectCommonName) {
        this.subjectCommonName = subjectCommonName;
    }

    public String getSubjectOrganizationDepartment() {
        return subjectOrganizationDepartment;
    }

    public void setSubjectOrganizationDepartment(String subjectOrganizationDepartment) {
        this.subjectOrganizationDepartment = subjectOrganizationDepartment;
    }

    public String getSubjectOrganizationName() {
        return subjectOrganizationName;
    }

    public void setSubjectOrganizationName(String subjectOrganizationName) {
        this.subjectOrganizationName = subjectOrganizationName;
    }

    public String getSubjectLocalityName() {
        return subjectLocalityName;
    }

    public void setSubjectLocalityName(String subjectLocalityName) {
        this.subjectLocalityName = subjectLocalityName;
    }

    public String getSubjectStateName() {
        return subjectStateName;
    }

    public void setSubjectStateName(String subjectStateName) {
        this.subjectStateName = subjectStateName;
    }

    public String getSubjectCountry() {
        return subjectCountry;
    }

    public void setSubjectCountry(String subjectCountry) {
        this.subjectCountry = subjectCountry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSigAlgName() {
        return sigAlgName;
    }

    public void setSigAlgName(String sigAlgName) {
        this.sigAlgName = sigAlgName;
    }

    public Date getNotValidBefore() {
        return notValidBefore;
    }

    public void setNotValidBefore(Date notValidBefore) {
        this.notValidBefore = notValidBefore;
    }

    public Date getNotValidAfter() {
        return notValidAfter;
    }

    public void setNotValidAfter(Date notValidAfter) {
        this.notValidAfter = notValidAfter;
    }

    public String getShaOne() {
        return shaOne;
    }

    public void setShaOne(String shaOne) {
        this.shaOne = shaOne;
    }

    public String getShaTwoFiveSix() {
        return shaTwoFiveSix;
    }

    public void setShaTwoFiveSix(String shaTwoFiveSix) {
        this.shaTwoFiveSix = shaTwoFiveSix;
    }

    public String getFullDetail() {
        return fullDetail;
    }

    public void setFullDetail(String fullDetail) {
        this.fullDetail = fullDetail;
    }

    @Override
    public String toString() {
        return "CertificateInfo{" +
                "requestId='" + requestId + '\'' +
                ", responseId='" + responseId + '\'' +
                ", issuerCommonName='" + issuerCommonName + '\'' +
                ", issuerOrganizationDepartment='" + issuerOrganizationDepartment + '\'' +
                ", issuerOrganizationName='" + issuerOrganizationName + '\'' +
                ", issuerLocalityName='" + issuerLocalityName + '\'' +
                ", issuerStateName='" + issuerStateName + '\'' +
                ", issuerCountry='" + issuerCountry + '\'' +
                ", subjectCommonName='" + subjectCommonName + '\'' +
                ", subjectOrganizationDepartment='" + subjectOrganizationDepartment + '\'' +
                ", subjectOrganizationName='" + subjectOrganizationName + '\'' +
                ", subjectLocalityName='" + subjectLocalityName + '\'' +
                ", subjectStateName='" + subjectStateName + '\'' +
                ", subjectCountry='" + subjectCountry + '\'' +
                ", type='" + type + '\'' +
                ", version=" + version +
                ", sigAlgName='" + sigAlgName + '\'' +
                ", notValidBefore=" + notValidBefore +
                ", notValidAfter=" + notValidAfter +
                ", shaOne='" + shaOne + '\'' +
                ", shaTwoFiveSix='" + shaTwoFiveSix + '\'' +
                ", fullDetail='" + fullDetail + '\'' +
                '}';
    }
}
