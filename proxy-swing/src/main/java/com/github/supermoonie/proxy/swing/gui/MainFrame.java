package com.github.supermoonie.proxy.swing.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.SVGUtils;
import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.gui.panel.SendRequestDialog;
import com.github.supermoonie.proxy.swing.gui.table.NoneEditTableModel;
import com.github.supermoonie.proxy.swing.gui.flow.*;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.gui.lintener.FlowMouseListener;
import com.github.supermoonie.proxy.swing.gui.lintener.ResponseCodeAreaShownListener;
import com.github.supermoonie.proxy.swing.gui.treetable.ListTreeTableNode;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-11-21
 */
public class MainFrame extends JFrame {

    // 菜单栏
    private JCheckBoxMenuItem recordMenuItem;
    private JCheckBoxMenuItem throttlingMenuItem;
    private JCheckBoxMenuItem remoteMapMenuItem;
    private JCheckBoxMenuItem blockListMenuItem;
    private JCheckBoxMenuItem allowListMenuItem;

    // Flow 显示的两种形式
    private JPanel structureTab;
    private JPanel sequenceTab;
    // Tree 的根结点
    private final FlowTreeNode rootNode = new FlowTreeNode();
    private final JTree flowTree = new JTree(rootNode);
    // List 容器
    private final FlowList flowList = new FlowList(new FilterListModel<>());

    private ListTreeTableNode overviewTreeTableRoot;
    private DefaultTreeTableModel overviewTreeTableModel;

    private JTabbedPane flowTabPane;

    private JTabbedPane requestTablePane;
    private JScrollPane requestHeaderScrollPane;
    private JTable requestHeaderTable;
    private JScrollPane requestQueryScrollPane;
    private JTable requestQueryTable;
    private JScrollPane requestContentTextScrollPane;
    private JTextArea requestContentTextArea;
    private JScrollPane requestFormScrollPane;
    private JTable requestFormTable;
    private RTextScrollPane requestJsonScrollPane;
    private RSyntaxTextArea requestJsonArea;
    private JScrollPane requestRawScrollPane;
    private JTextArea requestRawArea;
    private JTabbedPane responseTablePane;
    private JScrollPane responseHeaderScrollPane;
    private JTable responseHeaderTable;
    private JScrollPane responseTextAreaScrollPane;
    private JTextArea responseTextArea;
    private JPanel responseCodePane;
    private RSyntaxTextArea responseCodeArea;
    private JScrollPane responseRawScrollPane;
    private JTextArea responseRawArea;
    private JScrollPane responseImageScrollPane;
    private JPanel responseImagePane;


    public MainFrame() {
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
        splitPane.setDividerLocation(200);
        JPanel flowPanel = initLeftFlowPanel();
        JPanel flowDetailPanel = initRightFlowDetailPanel();
        splitPane.setLeftComponent(flowPanel);
        splitPane.setRightComponent(flowDetailPanel);
        container.add(splitPane, BorderLayout.CENTER);
        getContentPane().add(container, BorderLayout.CENTER);
    }

    /**
     * 初始化右侧面板
     *
     * @return {@link JPanel}
     */
    private JPanel initRightFlowDetailPanel() {
        // Flow detail 容器
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BorderLayout());
        JTabbedPane flowDetailTablePane = new JTabbedPane();
        // Overview tab
        JPanel overviewTab = new JPanel(new BorderLayout());
        overviewTreeTableRoot = new ListTreeTableNode(List.of("", ""));
        overviewTreeTableModel = new DefaultTreeTableModel(overviewTreeTableRoot, List.of("Name", "Value"));
        JXTreeTable overviewTreeTable = new JXTreeTable(overviewTreeTableModel);
        JScrollPane overviewScrollPane = new JScrollPane(overviewTreeTable);
        overviewScrollPane.setOpaque(false);
        overviewScrollPane.getViewport().setOpaque(false);
        overviewTreeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                overviewTreeTable.updateUI();
            }
        });
        overviewTreeTable.setDragEnabled(false);
        overviewTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        overviewTreeTable.setRowHeight(25);
        overviewTreeTable.setLeafIcon(SvgIcons.LEAF);
        overviewTreeTable.setEditable(false);
        overviewTreeTable.setShowGrid(false);
        overviewTreeTable.setFocusable(false);
        overviewTreeTable.setRootVisible(false);
        overviewTab.add(overviewScrollPane, BorderLayout.CENTER);
        flowDetailTablePane.addTab("Overview", overviewTab);
        // Content tab
        JPanel contentTab = new JPanel(new BorderLayout());
        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        requestTablePane = new JTabbedPane();
        requestHeaderTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        requestHeaderTable.setShowHorizontalLines(true);
        requestHeaderTable.setShowVerticalLines(true);
        requestHeaderScrollPane = new JScrollPane(requestHeaderTable);
        requestQueryTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        requestQueryScrollPane = new JScrollPane(requestQueryTable);
        requestFormTable = new JTable(new NoneEditTableModel(null, new String[]{"name", "Value"}));
        requestFormScrollPane = new JScrollPane(requestFormTable);
        requestContentTextArea = new JTextArea();
        requestContentTextArea.setEditable(false);
        requestContentTextScrollPane = new JScrollPane(requestContentTextArea);
        requestJsonArea = new RSyntaxTextArea();
        requestJsonArea.setCodeFoldingEnabled(true);
        requestJsonArea.setEditable(false);
        requestJsonScrollPane = new RTextScrollPane(requestJsonArea);
        requestRawArea = new JTextArea();
        requestRawArea.setEditable(false);
        requestRawScrollPane = new JScrollPane(requestRawArea);

        responseTablePane = new JTabbedPane();
        responseHeaderTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        responseHeaderScrollPane = new JScrollPane(responseHeaderTable);
        responseTextArea = new JTextArea();
        responseTextArea.setEditable(false);
        responseTextAreaScrollPane = new JScrollPane(responseTextArea);
        responseCodeArea = new RSyntaxTextArea();
        responseCodeArea.setCodeFoldingEnabled(true);
        responseCodeArea.setEditable(false);
        responseCodePane = new JPanel(new BorderLayout());
        responseCodePane.add(new RTextScrollPane(responseCodeArea));
        responseCodePane.addComponentListener(new ResponseCodeAreaShownListener());
        responseImagePane = new JPanel(new BorderLayout());
        responseImageScrollPane = new JScrollPane(responseImagePane);
        responseRawArea = new JTextArea();
        responseRawArea.setEditable(false);
        responseRawScrollPane = new JScrollPane(responseRawArea);
        contentSplitPane.setTopComponent(requestTablePane);
        contentSplitPane.setBottomComponent(responseTablePane);
        contentSplitPane.setDividerLocation(300);
        contentTab.add(contentSplitPane, BorderLayout.CENTER);
        flowDetailTablePane.addTab("Content", contentTab);
        detailPanel.add(flowDetailTablePane, BorderLayout.CENTER);
        return detailPanel;
    }

    /**
     * 初始化左侧面板
     *
     * @return {@link JPanel}
     */
    private JPanel initLeftFlowPanel() {
        // 左侧Flow panel
        JPanel flowPanel = new JPanel();
        flowPanel.setMinimumSize(new Dimension(200, 0));
        flowPanel.setLayout(new BorderLayout());
        JTextField filter = new JTextField();
        filter.addKeyListener(new FilterKeyListener(filter));
        flowPanel.add(filter, BorderLayout.NORTH);
        // Tab容器
        flowTabPane = new JTabbedPane();
        // Structure tab
        structureTab = new JPanel(new BorderLayout());
        structureTab.setMinimumSize(new Dimension(100, 0));
        structureTab.add(new JScrollPane(flowTree), BorderLayout.CENTER);
        flowTree.setRootVisible(false);
        flowTree.setShowsRootHandles(true);
        flowTree.setCellRenderer(new FlowTreeCellRender());
        flowTree.addMouseListener(new FlowMouseListener());
        // Sequence tab
        sequenceTab = new JPanel(new BorderLayout());
        sequenceTab.setMinimumSize(new Dimension(100, 0));
        flowList.setCellRenderer(new FlowListCellRenderer());
        flowList.addMouseListener(new FlowMouseListener());
        sequenceTab.add(new JScrollPane(flowList), BorderLayout.CENTER);
        flowTabPane.addTab("Structure", SvgIcons.TREE, structureTab);
        flowTabPane.addTab("Sequence", SvgIcons.LIST, sequenceTab);
        flowPanel.add(flowTabPane, BorderLayout.CENTER);
        return flowPanel;
    }


    private void initToolBar() {
        JPanel container = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(3, 5, 3, 5));
        JButton clearButton = new JButton();
        clearButton.setToolTipText("Clear");
        clearButton.setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/clear.svg"));
        clearButton.addActionListener(e -> {
            requestTablePane.removeAll();
            responseTablePane.removeAll();
            rootNode.removeAllChildren();
            flowList.clear();
            int childCount = overviewTreeTableModel.getChildCount(overviewTreeTableRoot);
            for (int i = childCount - 1; i >= 0; i--) {
                overviewTreeTableModel.removeNodeFromParent((MutableTreeTableNode) overviewTreeTableRoot.getChildAt(i));
            }
            flowTree.updateUI();
            MainFrameHelper.currentRequestId = -1;
            MainFrameHelper.firstFlow = true;
        });
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
        container.add(toolBar, BorderLayout.EAST);
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
        sendRequestMenuItem.addActionListener(e -> new SendRequestDialog(this, "Send Request", true));
        toolsMenu.add(sendRequestMenuItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem downloadRootCertificateMenuItem = new JMenuItem("Download Root Certificate");
        downloadRootCertificateMenuItem.addActionListener(e -> saveAsActionPerformed());
        helpMenu.add(downloadRootCertificateMenuItem);
        helpMenu.add(new JSeparator());
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> {
            if (ThemeManager.isDark()) {
                ThemeManager.setLightLookFeel();
            } else {
                ThemeManager.setDarkLookFeel();
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(proxyMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    public JScrollPane getResponseImageScrollPane() {
        return responseImageScrollPane;
    }

    public JPanel getResponseImagePane() {
        return responseImagePane;
    }

    public JTabbedPane getFlowTabPane() {
        return flowTabPane;
    }

    public JScrollPane getResponseHeaderScrollPane() {
        return responseHeaderScrollPane;
    }

    public JScrollPane getRequestHeaderScrollPane() {
        return requestHeaderScrollPane;
    }

    public JTabbedPane getResponseTablePane() {
        return responseTablePane;
    }

    public JTable getResponseHeaderTable() {
        return responseHeaderTable;
    }

    public JScrollPane getResponseTextAreaScrollPane() {
        return responseTextAreaScrollPane;
    }

    public JTextArea getResponseTextArea() {
        return responseTextArea;
    }

    public JPanel getResponseCodePane() {
        return responseCodePane;
    }

    public RSyntaxTextArea getResponseCodeArea() {
        return responseCodeArea;
    }

    public JScrollPane getResponseRawScrollPane() {
        return responseRawScrollPane;
    }

    public JTextArea getResponseRawArea() {
        return responseRawArea;
    }

    public RTextScrollPane getRequestJsonScrollPane() {
        return requestJsonScrollPane;
    }

    public RSyntaxTextArea getRequestJsonArea() {
        return requestJsonArea;
    }

    public JScrollPane getRequestRawScrollPane() {
        return requestRawScrollPane;
    }

    public JTextArea getRequestRawArea() {
        return requestRawArea;
    }

    public JScrollPane getRequestContentTextScrollPane() {
        return requestContentTextScrollPane;
    }

    public JTextArea getRequestContentTextArea() {
        return requestContentTextArea;
    }

    public JScrollPane getRequestFormScrollPane() {
        return requestFormScrollPane;
    }

    public JTable getRequestFormTable() {
        return requestFormTable;
    }

    public JScrollPane getRequestQueryScrollPane() {
        return requestQueryScrollPane;
    }

    public JTabbedPane getRequestTablePane() {
        return requestTablePane;
    }

    public JTable getRequestQueryTable() {
        return requestQueryTable;
    }

    public JTable getRequestHeaderTable() {
        return requestHeaderTable;
    }

    public JPanel getStructureTab() {
        return structureTab;
    }

    public JPanel getSequenceTab() {
        return sequenceTab;
    }

    public ListTreeTableNode getOverviewTreeTableRoot() {
        return overviewTreeTableRoot;
    }

    public DefaultTreeTableModel getOverviewTreeTableModel() {
        return overviewTreeTableModel;
    }

    public JTree getFlowTree() {
        return flowTree;
    }

    public FlowList getFlowList() {
        return flowList;
    }

    public FlowTreeNode getRootNode() {
        return rootNode;
    }
}
