package com.github.supermoonie.proxy.swing.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.SVGUtils;
import com.github.supermoonie.proxy.swing.ComponentModels;
import com.github.supermoonie.proxy.swing.gui.tree.FlowTreeNode;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @author supermoonie
 * @date 2020-11-21
 */
public class ProxyFrame extends JFrame {

    private JCheckBoxMenuItem recordMenuItem;
    private JCheckBoxMenuItem throttlingMenuItem;
    private JCheckBoxMenuItem remoteMapMenuItem;
    private JCheckBoxMenuItem blockListMenuItem;
    private JCheckBoxMenuItem allowListMenuItem;

    private final FlowTreeNode rootNode = new FlowTreeNode();
    private final JTree flowTree = new JTree(rootNode);

    public ProxyFrame() {
        setTitle("Lightning:10801");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        initMenuBar();
        initToolBar();
        initSplitPanel();
        setIconImages(SVGUtils.createWindowIconImages("/com/github/supermoonie/proxy/swing/icon/lighting.svg"));
    }

    @Override
    public void dispose() {
        super.dispose();
        FlatUIDefaultsInspector.hide();
    }

    private void initSplitPanel() {
        JPanel container = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(2);
        splitPane.setDividerLocation(200);

        // Flow panel
        JPanel flowPanel = new JPanel();
        flowPanel.setMinimumSize(new Dimension(200, 0));
        flowPanel.setLayout(new BorderLayout());
        flowPanel.add(new JTextField(), BorderLayout.NORTH);
        // Flow table panel
        JTabbedPane flowTablePane = new JTabbedPane();
        // Structure tab
        JPanel structureTab = new JPanel(new BorderLayout());
        structureTab.setMinimumSize(new Dimension(100, 0));
        DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value,
                        selected, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject().toString().equals("root") || node.getUserObject().toString().contains("path")) {
                    setIcon(getDefaultClosedIcon());
                } else {
                    if (node.getUserObject().toString().equals("html")) {
                        setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/html.svg"));
                    } else {
                        setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/js.svg"));
                    }
                }
                return c;
            }
        };
        structureTab.add(flowTree, BorderLayout.CENTER);
        flowTree.setRootVisible(false);
        flowTree.setShowsRootHandles(true);
//        flowTree.setCellRenderer(defaultTreeCellRenderer);

        JPanel sequenceTab = new JPanel(new BorderLayout());
        sequenceTab.setMinimumSize(new Dimension(100, 0));
        JList<String> flowList = new JList<>(new String[]{"foo", "bar"});
        sequenceTab.add(flowList, BorderLayout.CENTER);
        flowTablePane.addTab("Structure", new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/tree.svg"), structureTab);
        flowTablePane.addTab("Sequence", new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/list.svg"), sequenceTab);
        flowPanel.add(flowTablePane, BorderLayout.CENTER);

        // Flow detail panel
        JPanel detailPanel = new JPanel();
        detailPanel.setMinimumSize(new Dimension(200, 0));
        detailPanel.setLayout(new BorderLayout());
        JTabbedPane flowDetailTablePane = new JTabbedPane();
        JPanel overviewTab = new JPanel(new BorderLayout());
        Window window = SwingUtilities.getWindowAncestor(this);
        JXTreeTable jxTreeTable = new JXTreeTable(ComponentModels.getTreeTableModel(window != null ? window : this));
        JScrollPane overviewScrollPane = new JScrollPane(jxTreeTable);
//        jxTreeTable.setSelectionBackground(Color.decode("#2675BF"));

        jxTreeTable.setEditable(false);
        jxTreeTable.setDoubleBuffered(false);
        jxTreeTable.setShowGrid(false);
        jxTreeTable.setFocusable(false);
        jxTreeTable.setRootVisible(false);
        overviewTab.add(overviewScrollPane, BorderLayout.CENTER);
        flowDetailTablePane.addTab("Overview", overviewTab);
        JPanel contentTab = new JPanel(new BorderLayout());
        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JTabbedPane requestTablePane = new JTabbedPane();
        JPanel requestHeaderTab = new JPanel(new BorderLayout());
        Object[][] tableDate = new Object[5][8];
        for (int i = 0; i < 5; i++) {
            tableDate[i][0] = "1000" + i;
            for (int j = 1; j < 8; j++) {
                tableDate[i][j] = 0;
            }
        }
        String[] name = {"学号", "软件工程", "Java", "网络", "数据结构", "数据库", "总成绩", "平均成绩"};
        JTable requestHeaderTable = new JTable(tableDate, name);
        requestHeaderTab.add(new JScrollPane(requestHeaderTable), BorderLayout.CENTER);
        requestTablePane.addTab("Header", requestHeaderTab);
        JTabbedPane responseTablePane = new JTabbedPane();
        contentSplitPane.setTopComponent(requestTablePane);
        contentSplitPane.setBottomComponent(responseTablePane);
        contentTab.add(contentSplitPane, BorderLayout.CENTER);
        flowDetailTablePane.addTab("Content", contentTab);
        detailPanel.add(flowDetailTablePane, BorderLayout.CENTER);
        splitPane.setLeftComponent(flowPanel);
        splitPane.setRightComponent(detailPanel);
        container.add(splitPane, BorderLayout.CENTER);

        getContentPane().add(container, BorderLayout.CENTER);
    }

    private void initToolBar() {
        JPanel container = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(3, 5, 3, 5));
        JButton clearButton = new JButton();
        clearButton.setToolTipText("Clear");
        clearButton.setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/clear.svg"));
        JButton editButton = new JButton();
        editButton.setToolTipText("Edit");
        editButton.setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/edit.svg"));
        JButton repeatButton = new JButton();
        repeatButton.setToolTipText("Repeat");
        repeatButton.setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/repeat.svg"));
        JButton throttlingButton = new JButton();
        throttlingButton.setToolTipText("Throttling");
        throttlingButton.setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/throttling_stop.svg"));
        JButton recordButton = new JButton();
        recordButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getFirstChild().getChildAt(0);
                node.setUserObject("js");
                flowTree.updateUI();
            }
        });
        recordButton.setToolTipText("Record");
        recordButton.setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/play.svg"));
        toolBar.add(clearButton);
        toolBar.add(editButton);
        toolBar.add(repeatButton);
        toolBar.add(throttlingButton);
        toolBar.add(recordButton);
        container.add(toolBar, BorderLayout.CENTER);
        container.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
        getContentPane().add(container, BorderLayout.NORTH);

    }

    private void openActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(this);
    }

    private void saveAsActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(this);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        openMenuItem.setMnemonic('O');
        openMenuItem.addActionListener(e -> openActionPerformed());
        fileMenu.add(openMenuItem);
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveMenuItem.setMnemonic('S');
        saveMenuItem.addActionListener(e -> saveAsActionPerformed());
        fileMenu.add(saveMenuItem);

        // Proxy menu
        JMenu proxyMenu = new JMenu("Proxy");
        recordMenuItem = new JCheckBoxMenuItem("Record");
        recordMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        recordMenuItem.setMnemonic('R');
        recordMenuItem.addActionListener(e -> openActionPerformed());
        proxyMenu.add(recordMenuItem);
        throttlingMenuItem = new JCheckBoxMenuItem("Throttling");
        throttlingMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        throttlingMenuItem.setMnemonic('T');
        throttlingMenuItem.addActionListener(e -> saveAsActionPerformed());
        proxyMenu.add(throttlingMenuItem);
        JCheckBoxMenuItem systemProxyMenuItem = new JCheckBoxMenuItem("System Proxy");
        systemProxyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        systemProxyMenuItem.setMnemonic('P');
        systemProxyMenuItem.addActionListener(e -> saveAsActionPerformed());
        proxyMenu.add(systemProxyMenuItem);
        proxyMenu.add(new JSeparator());
        JMenuItem throttlingSettingMenuItem = new JMenuItem("Throttling Setting");
        throttlingSettingMenuItem.addActionListener(e -> saveAsActionPerformed());
        proxyMenu.add(throttlingSettingMenuItem);
        JMenuItem proxySettingMenuItem = new JMenuItem("Proxy Setting");
        proxySettingMenuItem.addActionListener(e -> saveAsActionPerformed());
        proxyMenu.add(proxySettingMenuItem);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        remoteMapMenuItem = new JCheckBoxMenuItem("Remote Map");
        remoteMapMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        remoteMapMenuItem.addActionListener(e -> openActionPerformed());
        toolsMenu.add(remoteMapMenuItem);
        blockListMenuItem = new JCheckBoxMenuItem("Block List");
        blockListMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        blockListMenuItem.addActionListener(e -> saveAsActionPerformed());
        toolsMenu.add(blockListMenuItem);
        allowListMenuItem = new JCheckBoxMenuItem("Allow List");
        allowListMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        allowListMenuItem.addActionListener(e -> saveAsActionPerformed());
        toolsMenu.add(allowListMenuItem);
        toolsMenu.add(new JSeparator());
        JMenuItem jsonViewerMenuItem = new JMenuItem("JSON Viewer");
        jsonViewerMenuItem.addActionListener(e -> saveAsActionPerformed());
        toolsMenu.add(jsonViewerMenuItem);
        JMenuItem sendRequestMenuItem = new JMenuItem("Send Request");
        sendRequestMenuItem.addActionListener(e -> saveAsActionPerformed());
        toolsMenu.add(sendRequestMenuItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem downloadRootCertificateMenuItem = new JMenuItem("Download Root Certificate");
        downloadRootCertificateMenuItem.addActionListener(e -> saveAsActionPerformed());
        helpMenu.add(downloadRootCertificateMenuItem);
        helpMenu.add(new JSeparator());
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> saveAsActionPerformed());
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(proxyMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    public JTree getFlowTree() {
        return flowTree;
    }

    public FlowTreeNode getRootNode() {
        return rootNode;
    }
}
