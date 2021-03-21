package com.github.supermoonie.proxy.fx.ui.main.handler;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.ui.dialog.InfoDialog;
import com.github.supermoonie.proxy.fx.support.PropertyPair;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public class OverviewTreeTableViewMouseEventHandler implements EventHandler<MouseEvent> {

    private final Logger log = LoggerFactory.getLogger(OverviewTreeTableViewMouseEventHandler.class);

    private final TreeTableView<PropertyPair> overviewTreeTableView;

    public OverviewTreeTableViewMouseEventHandler(TreeTableView<PropertyPair> overviewTreeTableView) {
        this.overviewTreeTableView = overviewTreeTableView;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TreeItem<PropertyPair> selectedItem = overviewTreeTableView.getSelectionModel().getSelectedItem();
            if (null != selectedItem) {
                PropertyPair propertyPair = selectedItem.getValue();
                if (!StringUtils.isEmpty(propertyPair.getValue())) {
                    Stage stage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/dialog/InfoDialog.fxml"));
                    try {
                        Parent parent = fxmlLoader.load();
                        InfoDialog infoDialog = fxmlLoader.getController();
                        infoDialog.setStage(stage);
                        infoDialog.setText(propertyPair.getValue());
                        stage.setScene(new Scene(parent));
                        App.setCommonIcon(stage, propertyPair.getKey());
                        stage.initModality(Modality.NONE);
                        stage.initStyle(StageStyle.UTILITY);
                        stage.setX(event.getX() + 300);
                        stage.setY(event.getY() + 100);
                        stage.showAndWait();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
