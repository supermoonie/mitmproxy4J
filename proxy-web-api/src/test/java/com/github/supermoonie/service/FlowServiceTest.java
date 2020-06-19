package com.github.supermoonie.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.github.supermoonie.WithSpringBootTest;
import com.github.supermoonie.bo.Flow;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author supermoonie
 * @date 2020-06-11
 */
public class FlowServiceTest extends WithSpringBootTest {

    @Resource
    private FlowService flowService;

    @Test
    public void test_filter() {
        List<Flow> list = flowService.fetch(null, -1, null, DateTime.now().offset(DateField.HOUR, -1));
        System.out.println(JSONUtil.toJsonStr(list));
    }
}