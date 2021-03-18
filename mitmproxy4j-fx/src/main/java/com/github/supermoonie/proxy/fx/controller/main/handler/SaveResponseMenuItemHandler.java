package com.github.supermoonie.proxy.fx.controller.main.handler;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.controller.FlowNode;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.UrlUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class SaveResponseMenuItemHandler implements EventHandler<ActionEvent> {

    private final Logger log = LoggerFactory.getLogger(SaveResponseMenuItemHandler.class);

    private final TabPane tabPane;
    private final Tab structureTab;
    private final TreeView<FlowNode> treeView;
    private final ListView<FlowNode> listView;

//    private final RequestMapper requestMapper = ApplicationContextUtil.getBean(RequestMapper.class);
//    private final ResponseMapper responseMapper = ApplicationContextUtil.getBean(ResponseMapper.class);
//    private final ContentMapper contentMapper = ApplicationContextUtil.getBean(ContentMapper.class);

    public SaveResponseMenuItemHandler(TabPane tabPane, Tab structureTab, TreeView<FlowNode> treeView, ListView<FlowNode> listView) {
        this.tabPane = tabPane;
        this.structureTab = structureTab;
        this.treeView = treeView;
        this.listView = listView;
    }

    @Override
    public void handle(ActionEvent event) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        FlowNode node;
        if (selectedTab.equals(structureTab)) {
            TreeItem<FlowNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (null == selectedItem) {
                return;
            }
            node = selectedItem.getValue();
        } else {
            node = listView.getSelectionModel().getSelectedItem();
            if (node == null) {
                return;
            }
        }
//        Request request = requestMapper.selectById(node.getId());
//        QueryWrapper<Response> resQuery = new QueryWrapper<>();
//        resQuery.eq("request_id", request.getId());
//        Response response = responseMapper.selectOne(resQuery);
//        Content content = contentMapper.selectById(response.getContentId());
//        if (null != content && null != content.getContent() && content.getContent().length > 0) {
//            FileChooser fileChooser = new FileChooser();
//            String lastFragment = UrlUtil.getLastFragment(request.getUri());
//            if (null != lastFragment) {
//                fileChooser.setInitialFileName(lastFragment);
//            }
//            File file = fileChooser.showSaveDialog(App.getPrimaryStage());
//            try {
//                FileUtils.writeByteArrayToFile(file, content.getContent());
//            } catch (IOException e) {
//                log.error(e.getMessage(), e);
//                AlertUtil.error(e);
//            }
//        } else {
//            AlertUtil.info("Empty Response!");
//        }
    }
}
