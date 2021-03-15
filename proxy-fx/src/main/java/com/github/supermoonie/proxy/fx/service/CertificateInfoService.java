package com.github.supermoonie.proxy.fx.service;

import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;

import java.security.cert.Certificate;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
public interface CertificateInfoService {

    /**
     * save {@link Certificate} info
     *
     * @param certificates list of {@link Certificate}
     * @param requestId    {@link Request#getId()}
     * @param responseId   {@link Response#getId()}
     */
    void saveList(List<Certificate> certificates, String requestId, String responseId);
}
