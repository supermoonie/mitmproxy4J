package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.fx.codec.SHACoder;
import com.github.supermoonie.proxy.fx.entity.CertificateInfo;
import com.github.supermoonie.proxy.fx.mapper.CertificateInfoMapper;
import com.github.supermoonie.proxy.fx.service.CertificateInfoService;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.security.x509.X509CertImpl;

import javax.annotation.Resource;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class CertificateInfoServiceImpl implements CertificateInfoService {

    private final Logger log = LoggerFactory.getLogger(CertificateInfoServiceImpl.class);

    @Resource
    private CertificateInfoMapper certificateInfoMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int saveList(List<Certificate> certificates, String requestId, String responseId) {
        int effectRows = 0;
        for (Certificate certificate : certificates) {
            if (certificate instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate) certificate;
                CertificateInfo info = new CertificateInfo();
                info.setId(UUID.randomUUID().toString());
                info.setRequestId(requestId);
                info.setResponseId(responseId);
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
                info.setSerialNumber(cert.getSerialNumber().toString(16));
                info.setType(cert.getType());
                info.setVersion(cert.getVersion());
                info.setSigAlgName(cert.getSigAlgName());
                info.setNotValidBefore(cert.getNotBefore());
                info.setNotValidAfter(cert.getNotAfter());
                try {
                    info.setShaOne(Hex.toHexString(SHACoder.encodeSHA(cert.getEncoded())).toLowerCase());
                    info.setShaTwoFiveSix(Hex.toHexString(SHACoder.encodeSHA256(cert.getEncoded())).toLowerCase());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                info.setFullDetail(cert.toString());
                certificateInfoMapper.insert(info);
                effectRows = effectRows + 1;
            }
        }
        return effectRows;
    }

    private Map<String, String> parseSubject(String subject) {
        Map<String, String> result = new HashMap<>(8);
        String[] names = subject.split(", ");
        for (String name : names) {
            String[] arr = name.split("=");
            if (arr.length == 2) {
                result.put(arr[0], arr[1]);
            } else {
                result.put(arr[0], "");
            }
        }
        return result;
    }
}
