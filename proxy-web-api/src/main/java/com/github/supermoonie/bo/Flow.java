package com.github.supermoonie.bo;

import com.github.supermoonie.model.Header;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import lombok.Data;

import java.util.List;

/**
 * @author supermoonie
 * @date 2020-06-11
 */
@Data
public class Flow {

    private Request request;

    private List<HeaderBO> requestHeaders;

    private String requestContent;

    private Response response;

    private List<HeaderBO> responseHeaders;

    private String responseContent;
}
