package com.github.supermoonie.proxy.fx.service.impl;

import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @date 2020-11-16
 */
public class CertificateInfoServiceImplTest {

    // CN=Starfield Services Root Certificate Authority - G2, O="Starfield Technologies, Inc.", L=Scottsdale, ST=Arizona, C=US
    private final Pattern SUBJECT_PATTERN = Pattern.compile("([A-Za-z]{1,2}=.*),?");

    @Test
    public void test_pattern() {
        String txt = "CN=Starfield Services Root Certificate Authority - G2, O=\"Starfield Technologies, Inc.\", L=Scottsdale, ST=Arizona, C=US";
//        System.out.println(Arrays.toString(txt.split("=")));
//        Matcher matcher = SUBJECT_PATTERN.matcher(txt);
//        if (matcher.find()) {
//            System.out.println("...");
//            for (int i = 1; i <= matcher.groupCount(); i ++) {
//                System.out.println(matcher.group(i));
//            }
//        }
        System.out.println(parseSubject(txt));
    }

    public Map<String, String> parseSubject(String subject) {
        Map<String, String> result = new HashMap<>(8);
        List<String> list = new LinkedList<>();
        String[] names = subject.split("=");
        for (String name : names) {
            if (name.contains(", ")) {
                int index = name.lastIndexOf(", ");
                String value = name.substring(0, index).replaceAll("\"", "");
                String key = name.substring(index + 2, name.length());
                list.add(value);
                list.add(key);
            } else {
                list.add(name);
            }
        }
        for (int i = 0; i < list.size(); i ++) {
            result.put(list.get(i), list.get(i + 1));
            i = i + 1;
        }
        return result;
    }

}