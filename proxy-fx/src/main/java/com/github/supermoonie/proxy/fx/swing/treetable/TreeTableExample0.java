package com.github.supermoonie.proxy.fx.swing.treetable;/*
 * %W% %E%
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import com.github.supermoonie.proxy.fx.source.Icons;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * A TreeTable example, showing a JTreeTable, operating on the local file
 * system.
 *
 * @author Philip Milne
 * @version %I% %G%
 */

public class TreeTableExample0 {
    public static void main(String[] args) throws IOException {
        new TreeTableExample0();
    }

    public TreeTableExample0() throws IOException {
        JFrame frame = new JFrame("TreeTable");
        JTreeTable treeTable = new JTreeTable(new FileSystemModel());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) treeTable.getTree().getCellRenderer();
        Icon closedIcon = new ImageIcon(IOUtils.toByteArray(Icons.class.getResourceAsStream("/icon/close.png")));
        Icon openIcon = new ImageIcon(IOUtils.toByteArray(Icons.class.getResourceAsStream("/icon/open.png")));
        Icon leafIcon = new ImageIcon(IOUtils.toByteArray(Icons.class.getResourceAsStream("/icon/open.png")));
        renderer.setClosedIcon(closedIcon);
        renderer.setOpenIcon(openIcon);
        renderer.setLeafIcon(null);
        treeTable.getTree().setRootVisible(true);
        frame.getContentPane().add(new JScrollPane(treeTable));
        frame.pack();
        frame.setVisible(true);
    }
}
