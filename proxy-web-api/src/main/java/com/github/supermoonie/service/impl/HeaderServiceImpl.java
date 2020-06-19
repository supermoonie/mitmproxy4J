package com.github.supermoonie.service.impl;

import cn.hutool.core.lang.UUID;
import com.github.supermoonie.mapper.HeaderMapper;
import com.github.supermoonie.model.Header;
import com.github.supermoonie.service.HeaderService;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
@Service
@Slf4j
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
                header.setId(UUID.fastUUID().toString());
                header.setName(name);
                header.setValue(value);
                header.setRequestId(requestId);
                header.setResponseId(responseId);
                headerMapper.insert(header);
                count = count + 1;
                log.info("saved header: {}, key: {}, value: {}, requestId: {}, responseId: {}", header.getId(), name, value, requestId, responseId);
            }
        }
        return count;
    }
}
