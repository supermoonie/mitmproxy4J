package com.github.supermoonie.proxy.fx.support;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class HexContentFlow extends Flow {

    private String hexRequestContent;

    private String hexResponseContent;

    public String getHexRequestContent() {
        return hexRequestContent;
    }

    public void setHexRequestContent(String hexRequestContent) {
        this.hexRequestContent = hexRequestContent;
    }

    public String getHexResponseContent() {
        return hexResponseContent;
    }

    public void setHexResponseContent(String hexResponseContent) {
        this.hexResponseContent = hexResponseContent;
    }
}
