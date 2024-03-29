package com.github.supermoonie.proxy.fx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.codec.SHACoder;
import com.github.supermoonie.proxy.fx.entity.CertificateInfo;
import com.github.supermoonie.proxy.fx.entity.CertificateMap;
import com.github.supermoonie.proxy.fx.mapper.CertificateInfoMapper;
import com.github.supermoonie.proxy.fx.mapper.CertificateMapMapper;
import com.github.supermoonie.proxy.fx.service.CertificateInfoService;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Pattern;

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

    @Resource
    private CertificateMapMapper certificateMapMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void saveList(List<Certificate> certificates, String requestId, String responseId) {
        if (CollectionUtils.isEmpty(certificates)) {
            return;
        }
        for (Certificate certificate : certificates) {
            if (certificate instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate) certificate;
                String serialNumber = cert.getSerialNumber().toString(16).toUpperCase();
                CertificateMap certificateMap = new CertificateMap();
                certificateMap.setCertificateSerialNumber(serialNumber);
                certificateMap.setId(UUID.randomUUID().toString());
                certificateMap.setRequestId(requestId);
                certificateMap.setResponseId(responseId);
                certificateMapMapper.insert(certificateMap);
                QueryWrapper<CertificateInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("serial_number", serialNumber);
                CertificateInfo certificateInfo = certificateInfoMapper.selectOne(queryWrapper);
                if (null == certificateInfo) {
                    CertificateInfo info = new CertificateInfo();
                    info.setId(UUID.randomUUID().toString());
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
                    certificateInfoMapper.insert(info);
                }
            }
        }
    }

    private Map<String, String> parseSubject(String subject) {
        Map<String, String> result = new HashMap<>(8);
        List<String> list = new LinkedList<>();
        String[] names = subject.split("=");
        for (String name : names) {
            if (name.contains(", ")) {
                int index = name.lastIndexOf(", ");
                String value = name.substring(0, index).replaceAll("\"", "");
                String key = name.substring(index + 2, name.length());
                list.add(value);
                list.add(key);
            } else {
                list.add(name);
            }
        }
        for (int i = 0; i < list.size(); i ++) {
            result.put(list.get(i), list.get(i + 1));
            i = i + 1;
        }
        return result;
    }
}
