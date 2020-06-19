package com.github.supermoonie.service;

import com.github.supermoonie.bo.Flow;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;

import java.util.Date;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-06-11
 */
public interface FlowService {

    /**
     * 根据条件过滤请求
     *
     * @param host        域名 {@link Request#getHost()}
     * @param port        端口 {@link Request#getPort()}
     * @param contentType 响应类型 {@link Response#getContentType()}
     * @param start       开始时间 {@link Request#getTimeCreated()}
     * @return list of {@link Flow}
     */
    List<Flow> fetch(String host, Integer port, String contentType, Date start);
}
