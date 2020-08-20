package com.github.supermoonie.proxy.intercept.req;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public abstract class AbstractRequestIntercept implements RequestIntercept {

    AbstractRequestIntercept pre;

    AbstractRequestIntercept next;
}
