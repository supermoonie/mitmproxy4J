package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.mapper.HeaderMapper;
import com.github.supermoonie.proxy.fx.service.HeaderService;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class HeaderServiceImpl implements HeaderService {

    @Resource
    private HeaderMapper headerMapper;

    @Override
    public int saveHeaders(HttpHeaders headers, String requestId, String responseId) {
        Set<String> names = headers.names();
        int count = 0;
        for (String name : names) {
            List<String> valueList = headers.getAll(name);
            for (String value : valueList) {
                Header header = new Header();
                header.setId(UUID.randomUUID().toString());
                header.setName(name);
                header.setValue(value);
                header.setRequestId(requestId);
                header.setResponseId(responseId);
                headerMapper.insert(header);
                count = count + 1;
            }
        }
        return count;
    }
}
