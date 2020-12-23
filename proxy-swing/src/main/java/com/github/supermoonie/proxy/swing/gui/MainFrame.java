package com.github.supermoonie.proxy.swing.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.SVGUtils;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Content;
import com.github.supermoonie.proxy.swing.entity.Header;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.flow.*;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.gui.lintener.FlowMouseListener;
import com.github.supermoonie.proxy.swing.gui.lintener.ResponseCodeAreaShownListener;
import com.github.supermoonie.proxy.swing.gui.panel.ComposeDialog;
import com.github.supermoonie.proxy.swing.gui.panel.PreferencesDialog;
import com.github.supermoonie.proxy.swing.gui.table.NoneEditTableModel;
import com.github.supermoonie.proxy.swing.gui.treetable.ListTreeTableNode;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.github.supermoonie.proxy.swing.util.ClipboardUtil;
import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.dao.Dao;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

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
    /**
     * OverView的根结点
     */
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
        setTitle("Lightning:" + ProxyManager.getInternalProxy().getPort());
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        initMenuBar();
        initToolBar();
        initSplitPanel();
        setIconImages(SVGUtils.createWindowIconImages("/com/github/supermoonie/proxy/swing/icon/lighting.svg"));
        if (ThemeManager.isDark()) {
            ThemeManager.getCodeAreaDarkTheme().apply(requestJsonArea);
            ThemeManager.getCodeAreaDarkTheme().apply(responseCodeArea);
        } else {
            ThemeManager.getCodeAreaLightTheme().apply(requestJsonArea);
            ThemeManager.getCodeAreaLightTheme().apply(responseCodeArea);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        FlatUIDefaultsInspector.hide();
    }

    private void initSplitPanel() {
        JPanel container = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(205);
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
        requestJsonArea.setPaintTabLines(false);
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
        responseCodeArea.setPaintTabLines(false);
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
//        flowPanel.setMinimumSize(new Dimension(310, 0));
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
        JPopupMenu popup = new JPopupMenu();
        JMenuItem copyResponseMenuItem = new JMenuItem("Copy Response");
        JMenuItem saveResponseMenuItem = new JMenuItem("Save Response"){{
            addActionListener(e -> {
                JPanel selectedComponent = (JPanel) flowTabPane.getSelectedComponent();
                Flow flow;
                if (selectedComponent.equals(structureTab)) {
                    FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
                    if (null == node || !node.isLeaf()) {
                        return;
                    }
                    flow = (Flow) node.getUserObject();
                } else {
                    flow = flowList.getSelectedValue();
                    if (null == flow) {
                        return;
                    }
                }
                Integer responseId = flow.getResponseId();
                if (null == responseId) {
                    return;
                }
                Integer requestId = flow.getRequestId();
                Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
                Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
                Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
                Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Map<String, Object> map = new HashMap<>(5);
                    Request request = requestDao.queryForId(requestId);
                    Map<String, Object> requestMap = new HashMap<>(16);
                    requestMap.put("url", request.getUri());
                    requestMap.put("method", request.getMethod());
                    requestMap.put("httpVersion", request.getHttpVersion());
                    requestMap.put("host", request.getHost());
                    requestMap.put("port", request.getPort());
                    requestMap.put("contentType", request.getContentType());
                    requestMap.put("startTime", dateFormat.format(request.getStartTime()));
                    requestMap.put("endTime", dateFormat.format(request.getEndTime()));
                    List<Header> requestHeaderList = headerDao.queryBuilder().where().eq(Header.REQUEST_ID_FIELD_NAME, requestId)
                            .and().isNull(Header.RESPONSE_ID_FIELD_NAME).query();
                    List<Map<String, String>> requestHeaders = new ArrayList<>();
                    for (Header header : requestHeaderList) {
                        requestHeaders.add(Map.of("name", header.getName(), "value", header.getValue()));
                    }
                    requestMap.put("header", requestHeaders);
                    if (null != request.getContentId()) {
                        Content content = contentDao.queryForId(request.getContentId());
                        requestMap.put("content", Hex.encodeHexString(content.getRawContent()));
                    }
                    map.put("request", requestMap);
                    Map<String, Object> responseMap = new HashMap<>(16);
                    Response response = responseDao.queryForId(responseId);
                    responseMap.put("contentType", response.getContentType());
                    responseMap.put("status", response.getStatus());
                    responseMap.put("httpVersion", response.getHttpVersion());
                    responseMap.put("startTime", dateFormat.format(response.getStartTime()));
                    responseMap.put("endTime", dateFormat.format(response.getEndTime()));
                    List<Header> responseHeaderList = headerDao.queryBuilder().where().eq(Header.REQUEST_ID_FIELD_NAME, requestId)
                            .and().eq(Header.RESPONSE_ID_FIELD_NAME, responseId).query();
                    List<Map<String, String>> responseHeaders = new ArrayList<>();
                    for (Header header : responseHeaderList) {
                        responseHeaders.add(Map.of("name", header.getName(), "value", header.getValue()));
                    }
                    responseMap.put("header", responseHeaders);
                    if (null != response.getContentId()) {
                        Content content = contentDao.queryForId(response.getContentId());
                        responseMap.put("content", Hex.encodeHexString(content.getRawContent()));
                    }
                    map.put("response", responseMap);

                    String json = Jackson.toJsonString(map, true);
                    JFileChooser fileChooser = new JFileChooser();
                    int i = fileChooser.showSaveDialog(this);
                    if (i == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        FileUtils.writeStringToFile(selectedFile, json, StandardCharsets.UTF_8);
                    }
                } catch (SQLException | IOException t) {
                    Application.showError(t);
                }
            });
        }};
        JMenuItem composeMenuItem = new JMenuItem("Compose");
        JMenuItem repeatMenuItem = new JMenuItem("Repeat");
        popup.add(new JMenuItem("Copy URL") {{
            addActionListener(e -> {
                JPanel selectedComponent = (JPanel) flowTabPane.getSelectedComponent();
                if (selectedComponent.equals(structureTab)) {
                    FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
                    if (null == node) {
                        return;
                    }
                    Flow flow = (Flow) node.getUserObject();
                    if (flow.getFlowType().equals(FlowType.BASE_URL)) {
                        ClipboardUtil.copyText(flow.getUrl());
                    } else {
                        List<String> urlList = new ArrayList<>();
                        urlList.add(flow.getUrl());
                        FlowTreeNode parent = (FlowTreeNode) node.getParent();
                        while (null != parent && !parent.equals(rootNode)) {
                            flow = (Flow) parent.getUserObject();
                            urlList.add(flow.getUrl());
                            parent = (FlowTreeNode) parent.getParent();
                        }
                        Collections.reverse(urlList);
                        String url = String.join("/", urlList);
                        ClipboardUtil.copyText(url);
                    }
                } else {
                    Flow flow = flowList.getSelectedValue();
                    if (null == flow) {
                        return;
                    }
                    ClipboardUtil.copyText(flow.getUrl());
                }
            });
        }});
        popup.add(copyResponseMenuItem);
        popup.add(saveResponseMenuItem);
        popup.add(new JSeparator());
        popup.add(composeMenuItem);
        popup.add(repeatMenuItem);
        popup.add(new JSeparator());
        popup.add(new JMenuItem("Block List"));
        popup.add(new JMenuItem("Allow List"));
        popup.add(new JSeparator());
        popup.add(new JMenuItem("Map Remote"));
        popup.add(new JMenuItem("Map Local"));
        flowTree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
                    if (null == node) {
                        return;
                    }
                    copyResponseMenuItem.setEnabled(node.isLeaf());
                    saveResponseMenuItem.setEnabled(node.isLeaf());
                    composeMenuItem.setEnabled(node.isLeaf());
                    repeatMenuItem.setEnabled(node.isLeaf());
                    popup.show((JComponent) e.getSource(), e.getX(), e.getY());
                }
            }
        });
        // Sequence tab
        sequenceTab = new JPanel(new BorderLayout());
        sequenceTab.setMinimumSize(new Dimension(100, 0));
        flowList.setCellRenderer(new FlowListCellRenderer());
        flowList.addMouseListener(new FlowMouseListener());
        flowList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Flow flow = flowList.getSelectedValue();
                    if (null == flow) {
                        return;
                    }
                    popup.show((JComponent) e.getSource(), e.getX(), e.getY());
                }
            }
        });
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
        JButton clearButton = new JButton();
        clearButton.setToolTipText("Clear");
        clearButton.setIcon(SvgIcons.CLEAR);
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
        JButton composeButton = new JButton();
        composeButton.setToolTipText("Compose");
        composeButton.setIcon(new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/edit.svg"));
        composeButton.addActionListener(e -> {
            Flow selectedFlow = MainFrameHelper.getSelectedFlow();
            new ComposeDialog(this, "Compose", selectedFlow, true);
        });
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
        toolBar.add(Box.createHorizontalStrut(20));
        toolBar.add(recordButton);
        toolBar.add(throttlingButton);
        toolBar.add(Box.createHorizontalStrut(20));
        toolBar.add(composeButton);
        toolBar.add(repeatButton);
        JPanel toolBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(0);
            setVgap(0);
            getInsets().set(0, 0, 0, 0);
        }}) {{
            getInsets().set(0, 0, 0, 0);
        }};
        toolBarPanel.add(toolBar);
        container.add(toolBarPanel, BorderLayout.CENTER);
        container.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        container.setPreferredSize(new Dimension(200, 28));
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
        fileMenu.add(new JSeparator());
        JMenuItem preferencesMenuItem = new JMenuItem("Preferences...");
        preferencesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        preferencesMenuItem.setMnemonic(',');
        preferencesMenuItem.addActionListener(e -> new PreferencesDialog(this, "Preference", true));
        fileMenu.add(preferencesMenuItem);

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
        JMenuItem composeMenuItem = new JMenuItem("Compose");
        composeMenuItem.addActionListener(e -> new ComposeDialog(this, "Compose", null, true));
        toolsMenu.add(composeMenuItem);
        JMenuItem jsonViewerMenuItem = new JMenuItem("JSON Viewer");
        jsonViewerMenuItem.addActionListener(e -> saveAsActionPerformed());
        toolsMenu.add(jsonViewerMenuItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem downloadRootCertificateMenuItem = new JMenuItem("Download Root Certificate");
        downloadRootCertificateMenuItem.addActionListener(e -> saveAsActionPerformed());
        helpMenu.add(downloadRootCertificateMenuItem);
        helpMenu.add(new JSeparator());
        JMenuItem aboutMenuItem = new JMenuItem("About");
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
