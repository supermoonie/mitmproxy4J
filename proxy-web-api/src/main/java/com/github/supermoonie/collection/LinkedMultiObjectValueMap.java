package com.github.supermoonie.collection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/6/25
 */
public class LinkedMultiObjectValueMap<K> extends LinkedHashMap<K, Object> {

    public LinkedMultiObjectValueMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinkedMultiObjectValueMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinkedMultiObjectValueMap() {
    }

    public LinkedMultiObjectValueMap(Map<? extends K, ?> m) {
        super(m);
    }

    public LinkedMultiObjectValueMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public void add(K key, Object value) {
        Object v = super.get(key);
        if (null == v) {
            super.put(key, value);
        } else {
            if (v instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) v;
                list.add(v);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(v);
                list.add(value);
                super.put(key, list);
            }
        }
    }

    public void addAll(Map<K, String[]> map) {
        map.forEach((key, values) -> {
            if (values.length == 1) {
                super.put(key, values[0]);
            } else {
                super.put(key, values);
            }
        });
    }
}
