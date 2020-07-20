package com.github.supermoonie.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.github.supermoonie.WithSpringBootTest;
import com.github.supermoonie.dto.FlowDTO;
import com.github.supermoonie.dto.FlowNode;
import com.github.supermoonie.util.JSON;
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
    public void test_fetch() {
        List<FlowNode> result = flowService.tree("weilekangnet", "", null, null);
        System.out.println(JSON.toJsonString(result));
    }
}