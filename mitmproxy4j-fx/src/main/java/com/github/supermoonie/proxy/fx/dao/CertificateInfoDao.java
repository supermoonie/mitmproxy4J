package com.github.supermoonie.proxy.fx.dao;

import com.github.supermoonie.proxy.fx.codec.SHACoder;
import com.github.supermoonie.proxy.fx.entity.CertificateInfo;
import com.github.supermoonie.proxy.fx.entity.CertificateMap;
import com.j256.ormlite.dao.Dao;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.*;

/**
 * @author supermoonie
 * @since 2020/11/25
 */
public final class CertificateInfoDao {

    private static final Logger log = LoggerFactory.getLogger(CertificateInfoDao.class);

    private CertificateInfoDao() {
        throw new UnsupportedOperationException();
    }

    public static void saveList(List<Certificate> certificates, Integer requestId, Integer responseId) throws SQLException {
        if (null == certificates || certificates.size() == 0) {
            return;
        }
        Dao<CertificateInfo, Integer> certificateInfoDao = DaoCollections.getDao(CertificateInfo.class);
        Dao<CertificateMap, Integer> certificateMapDao = DaoCollections.getDao(CertificateMap.class);
        for (Certificate certificate : certificates) {
            if (certificate instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate) certificate;
                String serialNumber = cert.getSerialNumber().toString(16).toUpperCase();
                CertificateMap certificateMap = new CertificateMap();
                certificateMap.setCertificateSerialNumber(serialNumber);
                certificateMap.setRequestId(requestId);
                certificateMap.setResponseId(responseId);
                certificateMap.setTimeCreated(new Date());
                certificateMapDao.create(certificateMap);
                List<CertificateInfo> certificateInfos = certificateInfoDao.queryForEq(CertificateInfo.SERIAL_NUMBER_FIELD_NAME, serialNumber);
                if (null == certificateInfos || certificateInfos.size() == 0) {
                    CertificateInfo info = new CertificateInfo();
                    String issuerName = cert.getIssuerDN().getName();
                    Map<String, String> issuerNames = parseSubject(issuerName);
                    info.setIssuerCommonName(issuerNames.getOrDefault("CN", ""));
                    info.setIssuerOrganizationDepartment(issuerNames.getOrDefault("OU", ""));
                    info.setIssuerOrganizationName(issuerNames.getOrDefault("O", ""));
                    info.setIssuerLocalityName(issuerNames.getOrDefault("L", ""));
                    info.setIssuerStateName(issuerNames.getOrDefault("ST", ""));
                    info.setIssuerCountry(issuerNames.getOrDefault("C", ""));
                    String subjectName = cert.getSubjectDN().getName();
                    Map<String, String> subjectNames = parseSubject(subjectName);
                    info.setSubjectCommonName(subjectNames.getOrDefault("CN", ""));
                    info.setSubjectOrganizationDepartment(subjectNames.getOrDefault("OU", ""));
                    info.setSubjectOrganizationName(subjectNames.getOrDefault("O", ""));
                    info.setSubjectLocalityName(subjectNames.getOrDefault("L", ""));
                    info.setSubjectStateName(subjectNames.getOrDefault("ST", ""));
                    info.setSubjectCountry(subjectNames.getOrDefault("C", ""));
                    info.setSerialNumber(serialNumber);
                    info.setType(cert.getType());
                    info.setVersion(cert.getVersion());
                    info.setSigAlgName(cert.getSigAlgName());
                    info.setNotValidBefore(cert.getNotBefore());
                    info.setNotValidAfter(cert.getNotAfter());
                    try {
                        info.setShaOne(Hex.toHexString(SHACoder.encodeSHA(cert.getEncoded())).toUpperCase());
                        info.setShaTwoFiveSix(Hex.toHexString(SHACoder.encodeSHA256(cert.getEncoded())).toUpperCase());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    info.setFullDetail(cert.toString());
                    info.setTimeCreated(new Date());
                    certificateInfoDao.create(info);
                }
            }
        }
    }

    private static Map<String, String> parseSubject(String subject) {
        Map<String, String> result = new HashMap<>(8);
        List<String> list = new LinkedList<>();
        String[] names = subject.split("=");
        for (String name : names) {
            if (name.contains(", ")) {
                int index = name.lastIndexOf(", ");
                String value = name.substring(0, index).replaceAll("\"", "");
                String key = name.substring(index + 2);
                list.add(value);
                list.add(key);
            } else {
                list.add(name);
            }
        }
        for (int i = 0; i < list.size() - 1; i++) {
            result.put(list.get(i), list.get(i + 1));
            i = i + 1;
        }
        return result;
    }
}
