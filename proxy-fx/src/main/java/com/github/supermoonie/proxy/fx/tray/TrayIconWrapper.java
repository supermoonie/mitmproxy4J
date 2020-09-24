package com.github.supermoonie.proxy.fx.tray;

import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/9/24
 */
public class TrayIconWrapper extends TrayIcon {

    private String name;

    public TrayIconWrapper(String name, Image image) {
        super(image);
        this.name = name;
    }

    public TrayIconWrapper(String name, Image image, String tooltip) {
        super(image, tooltip);
        this.name = name;
    }

    public TrayIconWrapper(String name, Image image, String tooltip, PopupMenu popup) {
        super(image, tooltip, popup);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
