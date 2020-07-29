package com.github.supermoonie.ws.response;

import com.github.supermoonie.dto.SimpleRequestDTO;
import lombok.Data;

import java.util.List;

/**
 * @author supermoonie
 * @date 2020-07-28
 */
@Data
public class SimpleRequestResponse {

    private List<SimpleRequestDTO> flows;
}
