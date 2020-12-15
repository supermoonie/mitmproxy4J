package com.github.supermoonie.proxy.swing.gui.table;

import java.io.File;

/**
 * @author supermoonie
 * @since 2020/12/12
 */
public class FormDataValue {

    private String textValue;

    private File file;

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
