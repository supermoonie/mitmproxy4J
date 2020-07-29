package com.github.supermoonie.ws;

import com.github.supermoonie.dto.SimpleRequestDTO;
import com.github.supermoonie.service.FlowService;
import com.github.supermoonie.ws.request.FetchRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-07-28
 */
@Controller("/flow")
public class FlowController {

    @Resource
    private FlowService flowService;

    @MessageMapping("/list")
    @SendTo("/topic/flow/list")
    public List<SimpleRequestDTO> list(FetchRequest request) {
        return flowService.list(request.getHost(), request.getMethod(), request.getStart(), request.getEnd());
    }
}
