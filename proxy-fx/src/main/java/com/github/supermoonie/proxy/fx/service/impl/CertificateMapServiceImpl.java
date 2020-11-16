package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.fx.service.CertificateMapService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author supermoonie
 * @date 2020-11-16
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class CertificateMapServiceImpl implements CertificateMapService {
}
