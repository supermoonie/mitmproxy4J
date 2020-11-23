package com.github.supermoonie.proxy.swing.support;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public class BlockUrl {

    private boolean enable;
    private String urlRegex;

    public BlockUrl() {
    }

    public BlockUrl(boolean enable, String urlRegex) {
        this.enable = enable;
        this.urlRegex = urlRegex;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getUrlRegex() {
        return urlRegex;
    }

    public void setUrlRegex(String urlRegex) {
        this.urlRegex = urlRegex;
    }
}
