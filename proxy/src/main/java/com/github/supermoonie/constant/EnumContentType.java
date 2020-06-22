package com.github.supermoonie.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author supermoonie
 * @since 2020/6/22
 */
public enum EnumContentType {

    /**
     * html_utf8
     */
    HTML_UTF8("text/html", StandardCharsets.UTF_8)
    ;

    private final String type;

    private final Charset charset;

    EnumContentType(String type, Charset charset) {
        this.type = type;
        this.charset = charset;
    }

    public String type() {
        return type;
    }

    public Charset charset() {
        return charset;
    }

    @Override
    public String toString() {
        return type + "; " + charset.toString();
    }
}
