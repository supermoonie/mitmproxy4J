package com.github.supermoonie.proxy.fx.constant;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/10/24
 */
public interface RequestRawType {

    String JSON = "JSON";

    String XML = "XML";

    String TEXT = "Text";

    String JAVASCRIPT = "JavaScript";

    String HTML = "HTML";

    List<String> RAW_TYPE_LIST = List.of(JSON, XML, TEXT, JAVASCRIPT, HTML);
}
