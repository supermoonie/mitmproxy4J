package com.github.supermoonie.proxy.fx.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.entity.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RequestMapperTest {

    @Resource
    private RequestMapper requestMapper;

    @Test
    public void selectSimple() {
    }

    @Test
    public void selectOne() {
        Request request = requestMapper.selectOne(new QueryWrapper<>());
        System.out.println(request);
    }
}