package com.github.supermoonie.proxy.fx.support;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.junit.Test;

import java.util.HashSet;

/**
 * @author supermoonie
 * @since 2020/11/9
 */
public class BlockUrlTest {

    @Test
    public void set() {
        ObservableSet<BlockUrl> blockUrls = FXCollections.observableSet(new HashSet<>());
        blockUrls.add(new BlockUrl(true, "foo"));
        blockUrls.add(new BlockUrl(true, "foo"));
        System.out.println(blockUrls);
    }

}