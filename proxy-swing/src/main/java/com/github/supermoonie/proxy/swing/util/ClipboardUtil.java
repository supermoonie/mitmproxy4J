package com.github.supermoonie.proxy.swing.util;

import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/8/22
 */
public class ClipboardUtil {

    private ClipboardUtil() {

    }

    public static void copyImage(Image image) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putImage(image);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public static void copyFile(List<File> files) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putFiles(files);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public static void copyText(String text) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(text);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
}
