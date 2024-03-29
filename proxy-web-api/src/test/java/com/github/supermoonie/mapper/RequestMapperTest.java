package com.github.supermoonie.mapper;

import com.github.supermoonie.WithSpringBootTest;
import com.github.supermoonie.mapper.dao.SimpleRequestDAO;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.util.JSON;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
public class RequestMapperTest extends WithSpringBootTest {

    @Resource
    private RequestMapper requestMapper;

    @Test
    public void insertTest() {
        Request request = new Request();
        request.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        request.setHttpVersion(HttpVersion.HTTP_1_1.text());
        request.setMethod(HttpMethod.GET.name());
        request.setUri("https://httpbin.org/get");
        requestMapper.insert(request);
    }

    @Test
    public void selectByIdTest() {
        Request request = requestMapper.selectById("ebd16a5b384343488657dd9472b077b8");
        System.out.println(request);
    }

    @Test
    public void selectSimple() {
        List<SimpleRequestDAO> list = requestMapper.selectSimple(null, null, "2020-07-28 07:36:37", null);
        System.out.println(JSON.toJsonString(list));
    }
}