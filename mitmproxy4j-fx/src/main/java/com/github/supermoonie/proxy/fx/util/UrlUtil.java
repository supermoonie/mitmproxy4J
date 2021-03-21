package com.github.supermoonie.proxy.fx.util;

import com.github.supermoonie.proxy.fx.controller.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public final class UrlUtil {

    private static final Logger log = LoggerFactory.getLogger(UrlUtil.class);

    private UrlUtil() {
    }

    public static String getLastFragment(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            if (null == path) {
                return null;
            }
            String[] fragment = path.split("/");
            if (fragment.length == 0) {
                return null;
            }
            String last = fragment[fragment.length - 1];
            if (null == last || "".equals(last)) {
                return null;
            }
            return last;
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<KeyValue> queryToList(String query) {
        List<KeyValue> list = new LinkedList<>();
        String[] params = query.split("&");
        for (String param : params) {
            KeyValue pair = queryFragmentToPair(param);
            if (null != pair) {
                list.add(pair);
            }
        }
        return list;
    }

    public static KeyValue queryFragmentToPair(String fragment) {
        String[] form = fragment.split("=");
        KeyValue pair = new KeyValue();
        if (form.length == 1) {
            pair.setKey(form[0]);
            return pair;
        } else if (form.length == 2) {
            pair.setKey(form[0]);
            pair.setValue(form[1]);
            return pair;
        }
        return null;
    }
}
