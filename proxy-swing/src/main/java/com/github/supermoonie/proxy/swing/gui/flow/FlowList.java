package com.github.supermoonie.proxy.swing.gui.flow;

import javax.swing.*;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author supermoonie
 * @since 2020/11/28
 */
public class FlowList extends JList<Flow> {

    private final FilterListModel<Flow> dataModel;

    public FlowList(FilterListModel<Flow> dataModel) {
        super(dataModel);
        this.dataModel = dataModel;
    }

    public void filter(Predicate<Flow> predicate) {
        dataModel.setPredicate(predicate);
        dataModel.filter();
    }

    public Optional<Flow> findFirst(int requestId) {
        return dataModel.getAll().stream().filter(item -> item.getRequestId() == requestId).findFirst();
    }

    public void add(Flow flow) {
        dataModel.addElement(flow);
    }

    public void clear() {
        dataModel.clear();
    }

}
