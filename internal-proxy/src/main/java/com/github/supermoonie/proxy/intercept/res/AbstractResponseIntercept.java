package com.github.supermoonie.proxy.intercept.res;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public abstract class AbstractResponseIntercept implements ResponseIntercept {

    AbstractResponseIntercept pre;

    AbstractResponseIntercept next;
}
