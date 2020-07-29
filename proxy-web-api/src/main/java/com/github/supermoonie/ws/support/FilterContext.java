package com.github.supermoonie.ws.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author supermoonie
 * @date 2020-07-29
 */
public class FilterContext {

    private static final Map<String, Filter> FILTER_MAP = new ConcurrentHashMap<>();

    public static void addFilter(String sessionId, Filter filter) {
        FILTER_MAP.put(sessionId, filter);
    }

    public static Map<String, Filter> getAll() {
        return FILTER_MAP;
    }

    public static Filter getFilter(String sessionId) {
        return FILTER_MAP.get(sessionId);
    }

    public static void removeFilter(String sessionId) {
        FILTER_MAP.remove(sessionId);
    }
}
