package com.github.supermoonie.controller.vo;

import com.github.supermoonie.dto.RequestDTO;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.util.JSON;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/7/5
 */
public class RequestDTOTest {

    @Test
    public void test_copy() {
        Request request = new Request();
        request.setId("11111111111111111");
        request.setTimeCreated(new Date());
        request.setContentType("application/json");
        request.setHttpVersion("HTTP/1.1");
        request.setUri("https://httpbin.org/get");
        request.setMethod("GET");
        request.setHost("httpbin");
        request.setPort(443);
        RequestDTO vo = new RequestDTO();
        BeanUtils.copyProperties(request, vo);
        System.out.println(JSON.toJsonString(vo));
    }

}