package com.github.supermoonie.proxy.fx.controller.component;

import javafx.scene.control.TreeItem;

import java.util.function.Predicate;

/**
 * @author supermoonie
 * @since 2020/10/16
 */
@FunctionalInterface
public interface TreeItemPredicate<T> {

    boolean test(TreeItem<T> parent, T value);

    static <T> TreeItemPredicate<T> create(Predicate<T> predicate) {
        return (parent, value) -> predicate.test(value);
    }

}