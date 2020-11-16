package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.fx.entity.CertificateInfo;
import com.github.supermoonie.proxy.fx.mapper.CertificateInfoMapper;
import com.github.supermoonie.proxy.fx.service.CertificateInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.security.x509.X509CertImpl;

import javax.annotation.Resource;
import java.security.cert.Certificate;
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

    final String cn = "CN";

    @Resource
    private CertificateInfoMapper certificateInfoMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int saveList(List<Certificate> certificates, String requestId, String responseId) {
        int effectRows = 0;
        for (Certificate certificate : certificates) {
            if (certificate instanceof X509CertImpl) {
                X509CertImpl cert = (X509CertImpl) certificate;
                CertificateInfo info = new CertificateInfo();
                info.setId(UUID.randomUUID().toString());
                info.setRequestId(requestId);
                info.setResponseId(responseId);
                String issuerName = cert.getIssuerDN().getName();
                Map<String, String> issuerNames = parseSubject(issuerName);
                info.setIssuerCommonName(issuerNames.getOrDefault(cn, ""));

                certificateInfoMapper.insert(info);
                effectRows = effectRows + 1;
            }
        }
        return 0;
    }

    private Map<String, String> parseSubject(String subject) {
        Map<String, String> result = new HashMap<>();
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
