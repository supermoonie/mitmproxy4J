package com.github.supermoonie.proxy.fx.util;

import com.github.supermoonie.proxy.fx.dto.ColumnMap;
import com.github.supermoonie.proxy.fx.support.PropertyPair;

import java.util.LinkedList;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public final class UrlUtil {

    private UrlUtil() {}

    public static List<PropertyPair> queryToList(String query) {
        List<PropertyPair> list = new LinkedList<>();
        String[] params = query.split("&");
        for (String param : params) {
            PropertyPair pair = queryFragmentToPair(param);
            if (null != pair) {
                list.add(pair);
            }
        }
        return list;
    }

    public static PropertyPair queryFragmentToPair(String fragment) {
        String[] form = fragment.split("=");
        PropertyPair pair = new PropertyPair();
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
