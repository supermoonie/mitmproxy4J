package com.github.supermoonie.proxy.swing.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.SVGUtils;
import com.github.supermoonie.proxy.swing.gui.content.NoneEditTableModel;
import com.github.supermoonie.proxy.swing.gui.flow.*;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.gui.lintener.FlowMouseListener;
import com.github.supermoonie.proxy.swing.gui.overview.ListTreeTableNode;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

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

    private JPanel structureTab;
    private JPanel sequenceTab;

    private final FlowTreeNode rootNode = new FlowTreeNode();
    private final JTree flowTree = new JTree(rootNode);
    private final FlowList flowList = new FlowList(new FilterListModel<>());

    private ListTreeTableNode overviewTreeTableRoot;
    private DefaultTreeTableModel overviewTreeTableModel;

    private JTabbedPane requestTablePane;
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
        JTextField filter = new JTextField();
        filter.addKeyListener(new FilterKeyListener(filter));
        flowPanel.add(filter, BorderLayout.NORTH);
        // Flow table panel
        JTabbedPane flowTabPane = new JTabbedPane();
        // Structure tab
        structureTab = new JPanel(new BorderLayout());
        structureTab.setMinimumSize(new Dimension(100, 0));
        structureTab.add(flowTree, BorderLayout.CENTER);
        flowTree.setRootVisible(false);
        flowTree.setShowsRootHandles(true);
        flowTree.setCellRenderer(new FlowTreeCellRender());
        flowTree.addMouseListener(new FlowMouseListener(flowTabPane));
        // Sequence tab
        sequenceTab = new JPanel(new BorderLayout());
        sequenceTab.setMinimumSize(new Dimension(100, 0));
        flowList.setCellRenderer(new FlowListCellRenderer());
        flowList.addMouseListener(new FlowMouseListener(flowTabPane));
        sequenceTab.add(flowList, BorderLayout.CENTER);
        flowTabPane.addTab("Structure", SvgIcons.TREE, structureTab);
        flowTabPane.addTab("Sequence", SvgIcons.LIST, sequenceTab);
        flowPanel.add(flowTabPane, BorderLayout.CENTER);

        // Flow detail panel
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
        JPanel contentTab = new JPanel(new BorderLayout());
        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        requestTablePane = new JTabbedPane();
        requestHeaderTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        requestHeaderTable.setRowHeight(25);
        requestQueryTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        requestQueryTable.setRowHeight(25);
        requestQueryScrollPane = new JScrollPane(requestQueryTable);
        requestFormTable = new JTable(new NoneEditTableModel(null, new String[]{"name", "Value"}));
        requestFormTable.setRowHeight(25);
        requestFormScrollPane = new JScrollPane(requestFormTable);
        requestContentTextArea = new JTextArea();
        requestContentTextArea.setEditable(false);
        requestContentTextScrollPane = new JScrollPane(requestContentTextArea);
        requestJsonArea = new RSyntaxTextArea();
        requestJsonArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(
                    "/com/github/supermoonie/proxy/swing/light.xml"));
            theme.apply(requestJsonArea);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        requestJsonArea.setCodeFoldingEnabled(true);
//        requestJsonArea.setEditable(false);
        requestJsonScrollPane = new RTextScrollPane(requestJsonArea);
        requestRawArea = new JTextArea();
//        requestRawArea.setEditable(false);
        requestRawScrollPane = new JScrollPane(requestRawArea);
        requestTablePane.add("Header", new JScrollPane(requestHeaderTable));
        requestTablePane.add("Query", requestQueryScrollPane);
        requestTablePane.add("Text", requestContentTextScrollPane);
        requestTablePane.add("Form", requestFormScrollPane);
        requestTablePane.add("JSON", requestJsonScrollPane);
        requestTablePane.add("Raw", requestRawScrollPane);

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
