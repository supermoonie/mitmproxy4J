package com.github.supermoonie.proxy.swing.gui.panel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/12/20
 */
public class PreferencesDialogTest {

    @Test
    public void test_reg() {
        System.out.println("192.168.1.1111".matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"));
        System.out.println("https://httpbin.org/get".matches(".*httpbin.org/.*"));
    }

}