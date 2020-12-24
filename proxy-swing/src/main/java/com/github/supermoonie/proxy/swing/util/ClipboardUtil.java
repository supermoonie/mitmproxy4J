package com.github.supermoonie.proxy.swing.util;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.datatransfer.*;
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
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new ImageTransferable(image), null);
    }

    public static void copyFile(List<File> files) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new FileTransferable(files), null);
    }

    public static void copyText(String text) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new StringSelection(text), null);
    }

    private static class ImageTransferable implements Transferable {

        Image i;

        public ImageTransferable(Image i) {
            this.i = i;
        }

        @Override
        @Nonnull
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class FileTransferable implements Transferable {

        private final List<File> listOfFiles;

        public FileTransferable(List<File> files) {
            this.listOfFiles = files;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor.equals(flavor);
        }

        @Override
        @Nonnull
        public Object getTransferData(DataFlavor flavor) {
            return listOfFiles;
        }
    }
}
