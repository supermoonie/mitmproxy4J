package com.github.supermoonie.proxy.swing.entity;

import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
@DatabaseTable(tableName = "certificate_info")
public class CertificateInfo {

    public static final String SERIAL_NUMBER_FIELD_NAME = "serial_number";
    public static final String ISSUER_COMMON_NAME_FIELD_NAME = "issuer_common_name";
    public static final String ISSUER_ORGANIZATION_DEPARTMENT_FIELD_NAME = "issuer_organization_department";
    public static final String ISSUER_ORGANIZATION_NAME_FIELD_NAME = "issuer_organization_name";
    public static final String ISSUER_LOCALITY_NAME_FIELD_NAME = "issuer_locality_name";
    public static final String ISSUER_STATE_NAME_FIELD_NAME = "issuer_state_name";
    public static final String ISSUER_COUNTRY_FIELD_NAME = "issuer_country";
    public static final String SUBJECT_COMMON_NAME_FIELD_NAME = "subject_common_name";
    public static final String SUBJECT_ORGANIZATION_DEPARTMENT_FIELD_NAME = "subject_organization_department";
    public static final String SUBJECT_ORGANIZATION_NAME_FIELD_NAME = "subject_organization_name";
    public static final String SUBJECT_LOCALITY_NAME_FIELD_NAME = "subject_locality_name";
    public static final String SUBJECT_STATE_NAME_FIELD_NAME = "subject_state_name";
    public static final String SUBJECT_COUNTRY_FIELD_NAME = "subject_country";
    public static final String TYPE_FIELD_NAME = "type";
    public static final String VERSION_FIELD_NAME = "version";
    public static final String SIG_ALG_FIELD_NAME = "sig_alg_name";
    public static final String NOT_VALID_BEFORE_FIELD_NAME = "not_valid_before";
    public static final String NOT_VALID_AFTER_FIELD_NAME = "not_valid_after";
    public static final String SHA_ONE_FIELD_NAME = "sha_one";
    public static final String SHA_TWO_FIVE_SIX_FIELD_NAME = "sha_two_five_six";
    public static final String FULL_DETAIL_FIELD_NAME = "full_detail";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = SERIAL_NUMBER_FIELD_NAME, canBeNull = false, uniqueIndex = true, uniqueIndexName = "uk_serial_number")
    private String serialNumber;
    @DatabaseField(columnName = ISSUER_COMMON_NAME_FIELD_NAME)
    private String issuerCommonName;
    @DatabaseField(columnName = ISSUER_ORGANIZATION_DEPARTMENT_FIELD_NAME)
    private String issuerOrganizationDepartment;
    @DatabaseField(columnName = ISSUER_ORGANIZATION_NAME_FIELD_NAME)
    private String issuerOrganizationName;
    @DatabaseField(columnName = ISSUER_LOCALITY_NAME_FIELD_NAME)
    private String issuerLocalityName;
    @DatabaseField(columnName = ISSUER_STATE_NAME_FIELD_NAME)
    private String issuerStateName;
    @DatabaseField(columnName = ISSUER_COUNTRY_FIELD_NAME)
    private String issuerCountry;
    @DatabaseField(columnName = SUBJECT_COMMON_NAME_FIELD_NAME)
    private String subjectCommonName;
    @DatabaseField(columnName = SUBJECT_ORGANIZATION_DEPARTMENT_FIELD_NAME)
    private String subjectOrganizationDepartment;
    @DatabaseField(columnName = SUBJECT_ORGANIZATION_NAME_FIELD_NAME)
    private String subjectOrganizationName;
    @DatabaseField(columnName = SUBJECT_LOCALITY_NAME_FIELD_NAME)
    private String subjectLocalityName;
    @DatabaseField(columnName = SUBJECT_STATE_NAME_FIELD_NAME)
    private String subjectStateName;
    @DatabaseField(columnName = SUBJECT_COUNTRY_FIELD_NAME)
    private String subjectCountry;
    @DatabaseField(columnName = TYPE_FIELD_NAME)
    private String type;
    @DatabaseField(columnName = VERSION_FIELD_NAME)
    private Integer version;
    @DatabaseField(columnName = SIG_ALG_FIELD_NAME)
    private String sigAlgName;
    @DatabaseField(columnName = NOT_VALID_BEFORE_FIELD_NAME)
    private Date notValidBefore;
    @DatabaseField(columnName = NOT_VALID_AFTER_FIELD_NAME)
    private Date notValidAfter;
    @DatabaseField(columnName = SHA_ONE_FIELD_NAME)
    private String shaOne;
    @DatabaseField(columnName = SHA_TWO_FIVE_SIX_FIELD_NAME)
    private String shaTwoFiveSix;
    @DatabaseField(columnName = FULL_DETAIL_FIELD_NAME)
    private String fullDetail;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}
