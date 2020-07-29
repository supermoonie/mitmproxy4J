package com.github.supermoonie.config.support;

import java.security.Principal;

/**
 * @author supermoonie
 * @date 2020-07-29
 */
public class StompPrincipal implements Principal {

    private final String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
