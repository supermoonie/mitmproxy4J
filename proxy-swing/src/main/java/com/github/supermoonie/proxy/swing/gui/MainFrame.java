package com.github.supermoonie.proxy.swing.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.SVGUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.github.supermoonie.proxy.swing.MitmProxy4J;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.*;
import com.github.supermoonie.proxy.swing.gui.flow.*;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import com.github.supermoonie.proxy.swing.gui.lintener.FlowSelectionListener;
import com.github.supermoonie.proxy.swing.gui.lintener.ResponseCodeAreaShownListener;
import com.github.supermoonie.proxy.swing.gui.panel.ComposeDialog;
import com.github.supermoonie.proxy.swing.gui.panel.RequestMapDialog;
import com.github.supermoonie.proxy.swing.gui.panel.TextAreaDialog;
import com.github.supermoonie.proxy.swing.gui.panel.controller.*;
import com.github.supermoonie.proxy.swing.gui.popup.CodeAreaCopyMenuItem;
import com.github.supermoonie.proxy.swing.gui.popup.CodeAreaSelectAllMenuItem;
import com.github.supermoonie.proxy.swing.gui.popup.TextAreaPopupMenu;
import com.github.supermoonie.proxy.swing.gui.table.NoneEditTableModel;
import com.github.supermoonie.proxy.swing.gui.treetable.ListTreeTableNode;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.github.supermoonie.proxy.swing.proxy.intercept.DefaultLocalMapIntercept;
import com.github.supermoonie.proxy.swing.proxy.intercept.DefaultRemoteMapIntercept;
import com.github.supermoonie.proxy.swing.util.ClipboardUtil;
import com.github.supermoonie.proxy.swing.util.HttpClientUtil;
import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
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
    private JMenuItem remoteMapMenuItem;
    private JMenuItem localMapMenuItem;
    private JMenuItem blockListMenuItem;
    private JMenuItem allowListMenuItem;
    private final JMenuItem dnsMenuItem = new JMenuItem("DNS");
    private final JMenuItem exportRootCertificateMenuItem = new JMenuItem("Export Root Certificate");
    private final JMenuItem localAddressMenuItem = new JMenuItem("Local IP Address");
    // 工具栏
    private final JButton allButton = filterButton("All");
    private final JButton jsonFilterButton = filterButton("JSON");
    private final JButton htmlFilterButton = filterButton("HTML");
    private final JButton imageFilterButton = filterButton("Image");
    private final JButton xmlFilterButton = filterButton("XML");
    private final JButton throttlingButton = new JButton();


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


    public MainFrame(String title) {
        super(title);
//        setTitle("Lightning | Listening on " + ProxyManager.getInternalProxy().getPort());
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
        overviewTreeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                overviewTreeTable.updateUI();
            }
        });
        overviewTreeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        overviewTreeTable.setRowHeight(25);
        overviewTreeTable.setLeafIcon(SvgIcons.LEAF);
        overviewTreeTable.setEditable(false);
        overviewTreeTable.setShowGrid(false);
        overviewTreeTable.setFocusable(false);
        overviewTreeTable.setRootVisible(false);
        overviewTreeTable.setShowHorizontalLines(true);
        overviewTreeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = overviewTreeTable.getSelectedRow();
                    if (-1 == selectedRow) {
                        return;
                    }
                    Object name = overviewTreeTable.getValueAt(selectedRow, 0);
                    Object value = overviewTreeTable.getValueAt(selectedRow, 1);
                    if (null != value) {
                        TextAreaDialog textAreaDialog = new TextAreaDialog(null, name.toString(), true);
                        textAreaDialog.setLocation(e.getX() + 160, e.getY() + 160);
                        textAreaDialog.getTextArea().setText(value.toString());
                        textAreaDialog.getTextArea().setEditable(true);
                        textAreaDialog.getTextArea().setCaretPosition(0);
                        textAreaDialog.setVisible(true);
                    }
                }
            }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyUrlMenuItem = new JMenuItem("Copy Url") {{
            addActionListener(e -> {
                String url = getSelectUrl();
                if (null == url) {
                    return;
                }
                ClipboardUtil.copyText(url);
            });
        }};
        JMenuItem copyMenuItem = new JMenuItem("Copy Selection") {{
            addActionListener(e -> {
                int selectedRow = overviewTreeTable.getSelectedRow();
                if (-1 == selectedRow) {
                    return;
                }
                Object name = overviewTreeTable.getValueAt(selectedRow, 0);
                Object value = overviewTreeTable.getValueAt(selectedRow, 1);
                if (null != value) {
                    ClipboardUtil.copyText(name.toString() + " : " + value.toString());
                }
            });
        }};
        JMenuItem copyValueMenuItem = new JMenuItem("Copy Value") {{
            addActionListener(e -> {
                int selectedRow = overviewTreeTable.getSelectedRow();
                if (-1 == selectedRow) {
                    return;
                }
                Object value = overviewTreeTable.getValueAt(selectedRow, 1);
                if (null != value) {
                    ClipboardUtil.copyText(value.toString());
                }
            });
        }};
        JMenuItem repeatMenuItem = new JMenuItem("Repeat") {{
            addActionListener(e -> {
                Flow selectedFlow = getSelectedFlow();
                repeat(selectedFlow);
            });
        }};
        JMenuItem composeMenuItem = new JMenuItem("Compose") {{
            addActionListener(e -> {
                Flow selectedFlow = getSelectedFlow();
                if (null == selectedFlow) {
                    return;
                }
                new ComposeDialog(MainFrame.this, "Compose", selectedFlow, true);
            });
        }};
        popupMenu.add(copyUrlMenuItem);
        popupMenu.add(copyMenuItem);
        popupMenu.add(copyValueMenuItem);
        popupMenu.add(new JSeparator());
        popupMenu.add(repeatMenuItem);
        popupMenu.add(composeMenuItem);
        overviewTreeTable.setComponentPopupMenu(popupMenu);
        overviewTreeTable.setFocusable(true);
        overviewTreeTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean flag = (e.isControlDown() || e.isMetaDown()) && (e.getKeyCode() == KeyEvent.VK_C);
                if (flag) {
                    int selectedRow = overviewTreeTable.getSelectedRow();
                    if (-1 == selectedRow) {
                        return;
                    }
                    Object name = overviewTreeTable.getValueAt(selectedRow, 0);
                    Object value = overviewTreeTable.getValueAt(selectedRow, 1);
                    if (null != value) {
                        ClipboardUtil.copyText(name.toString() + " " + value.toString());
                    }
                }
            }
        });
        overviewTab.add(overviewScrollPane, BorderLayout.CENTER);
        flowDetailTablePane.addTab("Overview", overviewTab);
        // Content tab
        JPanel contentTab = new JPanel(new BorderLayout());
        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        requestTablePane = new JTabbedPane();
        requestHeaderTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        requestHeaderTable.setShowHorizontalLines(true);
        requestHeaderTable.setShowVerticalLines(true);
        addTablePopupMenu(requestHeaderTable);
        requestHeaderTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean flag = (e.isControlDown() || e.isMetaDown()) && (e.getKeyCode() == KeyEvent.VK_C);
                if (flag) {
                    ClipboardUtil.copySelectedRow(requestHeaderTable);
                }
            }
        });
        requestHeaderScrollPane = new JScrollPane(requestHeaderTable);
        requestQueryTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        addTablePopupMenu(requestQueryTable);
        requestQueryTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean flag = (e.isControlDown() || e.isMetaDown()) && (e.getKeyCode() == KeyEvent.VK_C);
                if (flag) {
                    ClipboardUtil.copySelectedRow(requestQueryTable);
                }
            }
        });
        requestQueryScrollPane = new JScrollPane(requestQueryTable);
        requestFormTable = new JTable(new NoneEditTableModel(null, new String[]{"name", "Value"}));
        addTablePopupMenu(requestFormTable);
        requestFormTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean flag = (e.isControlDown() || e.isMetaDown()) && (e.getKeyCode() == KeyEvent.VK_C);
                if (flag) {
                    ClipboardUtil.copySelectedRow(requestFormTable);
                }
            }
        });
        requestFormScrollPane = new JScrollPane(requestFormTable);
        requestContentTextArea = new JTextArea();
        requestContentTextArea.setEditable(false);
        requestContentTextArea.setComponentPopupMenu(new TextAreaPopupMenu(requestContentTextArea));
        requestContentTextScrollPane = new JScrollPane(requestContentTextArea);
        requestJsonArea = new RSyntaxTextArea();
        requestJsonArea.setCodeFoldingEnabled(true);
        requestJsonArea.setPaintTabLines(false);
        requestJsonArea.setEditable(false);
        requestJsonArea.setPopupMenu(new JPopupMenu() {{
            add(new CodeAreaCopyMenuItem("Copy", requestJsonArea));
            add(new CodeAreaSelectAllMenuItem("Select All", requestJsonArea));
        }});
        requestJsonScrollPane = new RTextScrollPane(requestJsonArea);
        requestRawArea = new JTextArea();
        requestRawArea.setEditable(false);
        requestRawArea.setComponentPopupMenu(new TextAreaPopupMenu(requestRawArea));
        requestRawScrollPane = new JScrollPane(requestRawArea);

        responseTablePane = new JTabbedPane();
        responseHeaderTable = new JTable(new NoneEditTableModel(null, new String[]{"Name", "Value"}));
        addTablePopupMenu(responseHeaderTable);
        responseHeaderTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean flag = (e.isControlDown() || e.isMetaDown()) && (e.getKeyCode() == KeyEvent.VK_C);
                if (flag) {
                    ClipboardUtil.copySelectedRow(responseHeaderTable);
                }
            }
        });
        responseHeaderScrollPane = new JScrollPane(responseHeaderTable);
        responseTextArea = new JTextArea();
        responseTextArea.setEditable(false);
        responseTextArea.setComponentPopupMenu(new TextAreaPopupMenu(responseTextArea));
        responseTextAreaScrollPane = new JScrollPane(responseTextArea);
        responseCodeArea = new RSyntaxTextArea();
        responseCodeArea.setCodeFoldingEnabled(true);
        responseCodeArea.setPaintTabLines(false);
        responseCodeArea.setEditable(false);
        responseCodeArea.setPopupMenu(new JPopupMenu() {{
            add(new CodeAreaCopyMenuItem("Copy", responseCodeArea));
            add(new CodeAreaSelectAllMenuItem("Select All", responseCodeArea));
        }});
        responseCodePane = new JPanel(new BorderLayout());
        responseCodePane.add(new RTextScrollPane(responseCodeArea));
        responseCodePane.addComponentListener(new ResponseCodeAreaShownListener());
        responseImagePane = new JPanel(new BorderLayout());
        responseImageScrollPane = new JScrollPane(responseImagePane);
        responseRawArea = new JTextArea();
        responseRawArea.setEditable(false);
        responseRawArea.setComponentPopupMenu(new TextAreaPopupMenu(responseRawArea));
        responseRawScrollPane = new JScrollPane(responseRawArea);
        contentSplitPane.setTopComponent(requestTablePane);
        contentSplitPane.setBottomComponent(responseTablePane);
        contentSplitPane.setDividerLocation(300);
        contentTab.add(contentSplitPane, BorderLayout.CENTER);
        flowDetailTablePane.addTab("Content", contentTab);
        detailPanel.add(flowDetailTablePane, BorderLayout.CENTER);
        return detailPanel;
    }

    private JPopupMenu addTablePopupMenu(JTable table) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copySelectMenuItem = new JMenuItem("Copy") {{
            addActionListener(e -> ClipboardUtil.copySelectedRow(table));
        }};
        JMenuItem selectAllMenuItem = new JMenuItem("Select All") {{
            addActionListener(e -> table.selectAll());
        }};
        popupMenu.add(copySelectMenuItem);
        popupMenu.add(selectAllMenuItem);
        table.setComponentPopupMenu(popupMenu);
        return popupMenu;
    }

    /**
     * 初始化左侧面板
     *
     * @return {@link JPanel}
     */
    private JPanel initLeftFlowPanel() {
        // 左侧Flow panel
        JPanel flowPanel = new JPanel();
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
        flowTree.addTreeSelectionListener(new FlowSelectionListener());
        JPopupMenu popup = new JPopupMenu();
        JMenuItem copyUrlMenuItem = new JMenuItem("Copy URL") {{
            addActionListener(e -> {
                String url = getSelectUrl();
                if (null == url) {
                    return;
                }
                ClipboardUtil.copyText(url);
            });
        }};
        JMenuItem copyResponseMenuItem = new JMenuItem("Copy Response") {{
            addActionListener(e -> {
                Flow flow = getSelectedFlow();
                if (null == flow) {
                    return;
                }
                Integer responseId = flow.getResponseId();
                if (null == responseId) {
                    return;
                }
                Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
                Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
                try {
                    Response response = responseDao.queryForId(responseId);
                    if (null == response.getContentId()) {
                        return;
                    }
                    Content content = contentDao.queryForId(response.getContentId());
                    if (response.getContentType().toLowerCase().startsWith("image/")) {
                        ClipboardUtil.copyImage(ImageIO.read(new ByteArrayInputStream(content.getRawContent())));
                    } else {
                        ClipboardUtil.copyText(new String(content.getRawContent(), StandardCharsets.UTF_8));
                    }
                } catch (SQLException | IOException t) {
                    MitmProxy4J.showError(t);
                }
            });
        }};
        JMenuItem saveResponseMenuItem = new JMenuItem("Save Response") {{
            addActionListener(e -> {
                Flow flow = getSelectedFlow();
                if (null == flow) {
                    return;
                }
                Integer responseId = flow.getResponseId();
                if (null == responseId) {
                    return;
                }
                Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
                Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
                try {
                    Response response = responseDao.queryForId(responseId);
                    if (null == response.getContentId()) {
                        return;
                    }
                    Content content = contentDao.queryForId(response.getContentId());
                    JFileChooser fileChooser = new JFileChooser();
                    int i = fileChooser.showSaveDialog(this);
                    if (i == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        FileUtils.writeByteArrayToFile(selectedFile, content.getRawContent());
                    }
                } catch (SQLException | IOException t) {
                    MitmProxy4J.showError(t);
                }
            });
        }};
        JMenuItem copyAllMenuItem = new JMenuItem("Copy All") {{
            addActionListener(e -> {
                Flow flow = getSelectedFlow();
                String json = flowToJson(flow);
                ClipboardUtil.copyText(json);
            });
        }};
        JMenuItem saveAllMenuItem = new JMenuItem("Save All") {{
            addActionListener(e -> {
                Flow flow = getSelectedFlow();
                String json = flowToJson(flow);
                JFileChooser fileChooser = new JFileChooser();
                int i = fileChooser.showSaveDialog(this);
                if (i == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        FileUtils.writeStringToFile(selectedFile, json, StandardCharsets.UTF_8);
                    } catch (IOException t) {
                        MitmProxy4J.showError(t);
                    }
                }
            });
        }};
        JMenuItem composeMenuItem = new JMenuItem("Compose") {{
            addActionListener(e -> {
                Flow selectedFlow = getSelectedFlow();
                if (null == selectedFlow) {
                    return;
                }
                new ComposeDialog(MainFrame.this, "Compose", selectedFlow, true);
            });
        }};
        JMenuItem repeatMenuItem = new JMenuItem("Repeat") {{
            addActionListener(e -> {
                Flow selectedFlow = getSelectedFlow();
                repeat(selectedFlow);
            });
        }};
        JMenuItem allowListMenuItem = new JMenuItem("Allow List") {{
            addActionListener(e -> {
                Flow flow = getSelectedFlow();
                if (null == flow) {
                    return;
                }
                Integer requestId = flow.getRequestId();
                Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
                Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
                try {
                    Request request = requestDao.queryForId(requestId);
                    AllowBlock allowBlock = new AllowBlock();
                    allowBlock.setEnable(AllowBlock.ENABLE);
                    allowBlock.setType(AllowBlock.TYPE_ALLOW);
                    allowBlock.setLocation(request.getUri());
                    allowBlock.setTimeCreated(new Date());
                    allowBlockDao.create(allowBlock);
                } catch (SQLException t) {
                    MitmProxy4J.showError(t);
                }
            });
        }};
        JMenuItem blockListMenuItem = new JMenuItem("Block List") {{
            addActionListener(e -> {
                Flow flow = getSelectedFlow();
                if (null == flow) {
                    return;
                }
                Integer requestId = flow.getRequestId();
                Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
                Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
                try {
                    Request request = requestDao.queryForId(requestId);
                    AllowBlock allowBlock = new AllowBlock();
                    allowBlock.setEnable(AllowBlock.ENABLE);
                    allowBlock.setType(AllowBlock.TYPE_BLOCK);
                    allowBlock.setLocation(request.getUri());
                    allowBlock.setTimeCreated(new Date());
                    allowBlockDao.create(allowBlock);
                } catch (SQLException t) {
                    MitmProxy4J.showError(t);
                }
            });
        }};
        JMenuItem mapRemoteMenuItem = new JMenuItem("Map Remote") {{
            addActionListener(e -> {
                String url = getSelectUrl();
                if (null == url) {
                    return;
                }
                RequestMapDialog mapRemoteDialog = new RequestMapDialog(MainFrame.this, "Map Remote", true);
                mapRemoteDialog.getFromTextField().setText(url);
                mapRemoteDialog.getFromTextField().setEditable(false);
                mapRemoteDialog.getToTextField().requestFocus();
                mapRemoteDialog.getConfirmButton().addActionListener(event -> {
                    String to = mapRemoteDialog.getToTextField().getText();
                    if (null != to && !"".equals(to)) {
                        if (!to.matches("^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$")) {
                            JOptionPane.showMessageDialog(this, "Invalid Url!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        try {
                            Dao<RequestMap, Integer> requestMapDao = DaoCollections.getDao(RequestMap.class);
                            RequestMap requestMap = new RequestMap();
                            requestMap.setEnable(RequestMap.ENABLE);
                            requestMap.setTimeCreated(new Date());
                            requestMap.setMapType(RequestMap.TYPE_REMOTE);
                            requestMap.setToUrl(to);
                            requestMap.setFromUrl(url);
                            requestMapDao.create(requestMap);
                            DefaultRemoteMapIntercept.INSTANCE.getRemoteUriMap().put(url, to);
                        } catch (SQLException t) {
                            MitmProxy4J.showError(t);
                        } finally {
                            mapRemoteDialog.setVisible(false);
                        }
                    }
                });
                mapRemoteDialog.setVisible(true);
            });
        }};
        JMenuItem mapLocalMenuItem = new JMenuItem("Map Local") {{
            addActionListener(e -> {
                String url = getSelectUrl();
                if (null == url) {
                    return;
                }
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setMultiSelectionEnabled(false);
                RequestMapDialog mapLocalDialog = new RequestMapDialog(MainFrame.this, "Map Local", true);
                mapLocalDialog.getFromTextField().setText(url);
                mapLocalDialog.getFromTextField().setEditable(false);
                mapLocalDialog.getToTextField().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (fileChooser.showOpenDialog(mapLocalDialog) == JFileChooser.APPROVE_OPTION) {
                            String file = fileChooser.getSelectedFile().getAbsolutePath();
                            mapLocalDialog.getToTextField().setText(file);
                        }
                        e.consume();
                    }
                });
                mapLocalDialog.getConfirmButton().addActionListener(event -> {
                    String to = mapLocalDialog.getToTextField().getText();
                    if (null != to && !"".equals(to) && (new File(to).exists())) {
                        try {
                            Dao<RequestMap, Integer> requestMapDao = DaoCollections.getDao(RequestMap.class);
                            RequestMap requestMap = new RequestMap();
                            requestMap.setEnable(RequestMap.ENABLE);
                            requestMap.setTimeCreated(new Date());
                            requestMap.setMapType(RequestMap.TYPE_LOCAL);
                            requestMap.setToUrl(to);
                            requestMap.setFromUrl(url);
                            requestMapDao.create(requestMap);
                            DefaultLocalMapIntercept.INSTANCE.getLocalMap().put(url, to);
                        } catch (SQLException t) {
                            MitmProxy4J.showError(t);
                        } finally {
                            mapLocalDialog.setVisible(false);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Path!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                mapLocalDialog.setVisible(true);
            });
        }};
        popup.add(copyUrlMenuItem);
        popup.add(copyAllMenuItem);
        popup.add(saveAllMenuItem);
        popup.add(copyResponseMenuItem);
        popup.add(saveResponseMenuItem);
        popup.add(new JSeparator());
        popup.add(composeMenuItem);
        popup.add(repeatMenuItem);
        popup.add(new JSeparator());
        popup.add(allowListMenuItem);
        popup.add(blockListMenuItem);
        popup.add(new JSeparator());
        popup.add(mapRemoteMenuItem);
        popup.add(mapLocalMenuItem);
        flowTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
                    if (null == node) {
                        return;
                    }
                    copyResponseMenuItem.setEnabled(node.isLeaf());
                    saveResponseMenuItem.setEnabled(node.isLeaf());
                    copyAllMenuItem.setEnabled(node.isLeaf());
                    saveAllMenuItem.setEnabled(node.isLeaf());
                    composeMenuItem.setEnabled(node.isLeaf());
                    repeatMenuItem.setEnabled(node.isLeaf());
                    allowListMenuItem.setEnabled(node.isLeaf());
                    blockListMenuItem.setEnabled(node.isLeaf());
                }
            }
        });
        flowTree.setComponentPopupMenu(popup);
        // Sequence tab
        sequenceTab = new JPanel(new BorderLayout());
        sequenceTab.setMinimumSize(new Dimension(100, 0));
        flowList.setCellRenderer(new FlowListCellRenderer());
        flowList.addListSelectionListener(new FlowSelectionListener());
        flowList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Flow flow = flowList.getSelectedValue();
                    if (null == flow) {
                        return;
                    }
                    copyResponseMenuItem.setEnabled(true);
                    saveResponseMenuItem.setEnabled(true);
                    copyAllMenuItem.setEnabled(true);
                    saveAllMenuItem.setEnabled(true);
                    composeMenuItem.setEnabled(true);
                    repeatMenuItem.setEnabled(true);
                    allowListMenuItem.setEnabled(true);
                    blockListMenuItem.setEnabled(true);
                }
            }
        });
        flowList.setComponentPopupMenu(popup);
        sequenceTab.add(new JScrollPane(flowList), BorderLayout.CENTER);
        flowTabPane.addTab("Structure", SvgIcons.TREE, structureTab);
        flowTabPane.addTab("Sequence", SvgIcons.LIST, sequenceTab);
        flowPanel.add(flowTabPane, BorderLayout.CENTER);
        return flowPanel;
    }

    private List<Map<String, Object>> certificateInfo(Integer requestId, Integer responseId) throws SQLException {
        Dao<CertificateMap, Integer> certificateMapDao = DaoCollections.getDao(CertificateMap.class);
        Dao<CertificateInfo, Integer> certificateInfoDao = DaoCollections.getDao(CertificateInfo.class);
        Where<CertificateMap, Integer> certificateMapWhere = certificateMapDao.queryBuilder().where().eq(CertificateMap.REQUEST_ID_FIELD_NAME, requestId);
        if (null == responseId) {
            certificateMapWhere.and().isNull(CertificateMap.RESPONSE_ID_FIELD_NAME);
        } else {
            certificateMapWhere.and().eq(CertificateMap.RESPONSE_ID_FIELD_NAME, responseId);
        }
        List<CertificateMap> localCertificateMaps = certificateMapWhere.query();
        List<Map<String, Object>> localCertificateList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (CertificateMap localCertMap : localCertificateMaps) {
            CertificateInfo certificateInfo = certificateInfoDao.queryBuilder().where().eq(CertificateInfo.SERIAL_NUMBER_FIELD_NAME, localCertMap.getCertificateSerialNumber()).queryForFirst();
            Map<String, Object> localCert = new HashMap<>();
            localCert.put("serialNumber", certificateInfo.getSerialNumber());
            localCert.put("type", certificateInfo.getType());
            localCert.put("issuedTo", Map.of(
                    "commonName", certificateInfo.getSubjectCommonName(),
                    "organizationUnit", certificateInfo.getSubjectOrganizationDepartment(),
                    "organizationName", certificateInfo.getSubjectOrganizationName(),
                    "localityName", certificateInfo.getSubjectLocalityName(),
                    "stateName", certificateInfo.getSubjectStateName(),
                    "country", certificateInfo.getSubjectCountry()
            ));
            localCert.put("issuedBy", Map.of(
                    "commonName", certificateInfo.getIssuerCommonName(),
                    "organizationUnit", certificateInfo.getIssuerOrganizationDepartment(),
                    "organizationName", certificateInfo.getIssuerOrganizationName(),
                    "localityName", certificateInfo.getIssuerLocalityName(),
                    "stateName", certificateInfo.getIssuerStateName(),
                    "country", certificateInfo.getIssuerCountry()
            ));
            localCert.put("notValidBefore", dateFormat.format(certificateInfo.getNotValidBefore()));
            localCert.put("notValidAfter", dateFormat.format(certificateInfo.getNotValidAfter()));
            localCert.put("fingerprints", Map.of(
                    "SHA-1", certificateInfo.getShaOne(),
                    "SHA-256", certificateInfo.getShaOne()
            ));
            localCert.put("detail", certificateInfo.getFullDetail());
            localCertificateList.add(localCert);
        }
        return localCertificateList;
    }

    private void initToolBar() {
        JPanel container = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton clearButton = new JButton();
        clearButton.setToolTipText("Clear");
        clearButton.setIcon(SvgIcons.CLEAR);
        clearButton.addActionListener(e -> {
            flowTree.clearSelection();
            flowList.clearSelection();
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
        repeatButton.setIcon(SvgIcons.REPEAT);
        repeatButton.addActionListener(e -> {
            Flow flow = getSelectedFlow();
            repeat(flow);
        });
        throttlingButton.setToolTipText("Throttling");
        throttlingButton.setIcon(SvgIcons.THROTTLING);
        throttlingButton.addActionListener(e -> {
            boolean enableThrottling = ProxyManager.getInternalProxy().isTrafficShaping();
            ProxyManager.enableLimit(!enableThrottling);
            throttlingButton.setSelected(!enableThrottling);
        });
        JButton recordButton = new JButton();
        recordButton.addActionListener(e -> {
            MitmProxy4J.RECORD_FLAG.set(!MitmProxy4J.RECORD_FLAG.get());
            recordButton.setIcon(MitmProxy4J.RECORD_FLAG.get() ? SvgIcons.STOP : SvgIcons.PLAY);
            recordButton.setToolTipText(MitmProxy4J.RECORD_FLAG.get() ? "Stop" : "Start");
        });
        recordButton.setToolTipText("Stop");
        recordButton.setIcon(SvgIcons.STOP);
        toolBar.add(clearButton);
        toolBar.add(recordButton);
        toolBar.add(throttlingButton);
        toolBar.add(composeButton);
        toolBar.add(repeatButton);
        toolBar.add(Box.createHorizontalStrut(20));
        JPanel toolBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER) {{
            setHgap(0);
            setVgap(0);
            getInsets().set(0, 0, 0, 0);
        }}) {{
            getInsets().set(0, 0, 0, 0);
        }};
        toolBarPanel.add(toolBar);
        container.add(toolBarPanel, BorderLayout.EAST);
        JToolBar filterToolBar = new JToolBar();
        filterToolBar.setFloatable(false);
        allButton.setSelected(true);
        filterToolBar.add(allButton);
        filterToolBar.add(new JSeparator());
        filterToolBar.add(jsonFilterButton);
        filterToolBar.add(htmlFilterButton);
        filterToolBar.add(imageFilterButton);
        filterToolBar.add(xmlFilterButton);
        container.add(filterToolBar, BorderLayout.WEST);
        container.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
        getContentPane().add(container, BorderLayout.NORTH);
    }

    private JButton filterButton(String name) {
        JButton button = new JButton(name);
        button.setMargin(new Insets(2, 10, 2, 10));
        return button;
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // Proxy menu
        JMenu proxyMenu = new JMenu("Proxy");
//        JCheckBoxMenuItem systemProxyMenuItem = new JCheckBoxMenuItem("System Proxy");
//        systemProxyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
//        systemProxyMenuItem.addActionListener(e -> {
//
//        });
//        proxyMenu.add(systemProxyMenuItem);
//        proxyMenu.add(new JSeparator());
        if (!SystemInfo.isMacOS) {
            JMenuItem appearanceMenuItem = new JMenuItem("Appearance");
            appearanceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            appearanceMenuItem.addActionListener(e -> new AppearanceDialogController(this, "Appearance", true).setVisible(true));
            proxyMenu.add(appearanceMenuItem);
        }
        JMenuItem proxySettingMenuItem = new JMenuItem("Proxy Setting");
        proxySettingMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        proxySettingMenuItem.addActionListener(e -> {
            int port = ApplicationPreferences.getState().getInt(ApplicationPreferences.KEY_PROXY_PORT, ApplicationPreferences.DEFAULT_PROXY_PORT);
            boolean auth = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_PROXY_AUTH, ApplicationPreferences.DEFAULT_PROXY_AUTH);
            String user = ApplicationPreferences.getState().get(ApplicationPreferences.KEY_PROXY_AUTH_USER, null);
            String pwd = ApplicationPreferences.getState().get(ApplicationPreferences.KEY_PROXY_AUTH_PWD, null);
            new ProxySettingDialogController(this, "Proxy Setting", true, port, auth, user, pwd).setVisible(true);
        });
        proxyMenu.add(proxySettingMenuItem);
        JMenuItem throttlingMenuItem = new JMenuItem("Throttling Setting");
        throttlingMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        throttlingMenuItem.addActionListener(e -> new ThrottlingDialogController(this, "Throttling", true).setVisible(true));
        proxyMenu.add(throttlingMenuItem);
        JMenuItem accessControlMenuItem = new JMenuItem("Access Control");
        accessControlMenuItem.addActionListener(e -> new AccessControlController(this, "Access Control", true).setVisible(true));
        proxyMenu.add(accessControlMenuItem);
        JMenuItem externalProxyMenuItem = new JMenuItem("External Proxy");
        externalProxyMenuItem.addActionListener(e -> new ExternalProxyDialogController(this, "External Proxy", true).setVisible(true));
        proxyMenu.add(externalProxyMenuItem);
        proxyMenu.add(dnsMenuItem);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        remoteMapMenuItem = new JMenuItem("Remote Map");
        remoteMapMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        remoteMapMenuItem.addActionListener(e -> new RemoteMapDialogController(this, "Remote Map", true).setVisible(true));
        toolsMenu.add(remoteMapMenuItem);
        localMapMenuItem = new JMenuItem("Local Map");
        localMapMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        localMapMenuItem.addActionListener(e -> new LocalMapDialogController(this, "Local Map", true).setVisible(true));
        toolsMenu.add(localMapMenuItem);
        blockListMenuItem = new JMenuItem("Block List");
        blockListMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        blockListMenuItem.addActionListener(e -> new BlockListDialogController(this, "Block List", true).setVisible(true));
        toolsMenu.add(blockListMenuItem);
        allowListMenuItem = new JMenuItem("Allow List");
        allowListMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        allowListMenuItem.addActionListener(e -> new AllowListDialogController(this, "Allow List", true).setVisible(true));
        toolsMenu.add(allowListMenuItem);
        toolsMenu.add(new JSeparator());
        JMenuItem composeMenuItem = new JMenuItem("Compose");
        composeMenuItem.addActionListener(e -> new ComposeDialog(this, "Compose", null, true));
        toolsMenu.add(composeMenuItem);
//        JMenuItem jsonViewerMenuItem = new JMenuItem("JSON Viewer");
//        jsonViewerMenuItem.addActionListener(e -> {
//
//        });
//        toolsMenu.add(jsonViewerMenuItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(exportRootCertificateMenuItem);
        helpMenu.add(localAddressMenuItem);
        helpMenu.add(new JSeparator());
        if (!SystemInfo.isMacOS) {
            JMenuItem aboutMenuItem = new JMenuItem("About");
            helpMenu.add(aboutMenuItem);
        }
        menuBar.add(proxyMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void repeat(Flow selectedFlow) {
        if (null == selectedFlow) {
            return;
        }
        MitmProxy4J.EXECUTOR.execute(() -> {
            try (CloseableHttpClient httpClient = HttpClientUtil.createTrustAllApacheHttpClientBuilder()
                    .setProxy(new HttpHost("127.0.0.1", ProxyManager.getInternalProxy().getPort()))
                    .build()) {
                Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
                Request request = requestDao.queryForId(selectedFlow.getRequestId());
                RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod().toUpperCase()).setUri(request.getUri());
                Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
                List<Header> headerList = headerDao.queryBuilder().where().eq(Header.REQUEST_ID_FIELD_NAME, request.getId())
                        .and().isNull(Header.RESPONSE_ID_FIELD_NAME).query();
                for (Header header : headerList) {
                    if (header.getName().toLowerCase().equals("content-length")) {
                        continue;
                    }
                    requestBuilder.addHeader(header.getName(), header.getValue());
                }
                if (null != request.getContentId()) {
                    Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
                    Content content = contentDao.queryForId(request.getContentId());
                    BasicHttpEntity entity = new BasicHttpEntity();
                    entity.setContent(new ByteArrayInputStream(content.getRawContent()));
                    requestBuilder.setEntity(entity);
                }
                httpClient.execute(requestBuilder.build()).close();
            } catch (Exception t) {
                MitmProxy4J.showError(t);
            }
        });
    }

    private String getSelectUrl() {
        JPanel selectedComponent = (JPanel) flowTabPane.getSelectedComponent();
        String url;
        if (selectedComponent.equals(structureTab)) {
            FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
            if (null == node) {
                return null;
            }
            Flow flow = (Flow) node.getUserObject();
            if (flow.getFlowType().equals(FlowType.BASE_URL)) {
                url = flow.getUrl();
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
                url = String.join("/", urlList);
            }
        } else {
            Flow flow = flowList.getSelectedValue();
            if (null == flow) {
                return null;
            }
            url = flow.getUrl();
        }
        return url;
    }

    private String flowToJson(Flow flow) {
        if (null == flow) {
            return null;
        }
        Integer responseId = flow.getResponseId();
        if (null == responseId) {
            return null;
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
            map.put("tls", Map.of("local", certificateInfo(requestId, null), "server", certificateInfo(requestId, responseId)));
            return Jackson.toJsonString(map, true);
        } catch (SQLException t) {
            MitmProxy4J.showError(t);
            return null;
        }
    }

    private Flow getSelectedFlow() {
        JPanel selectedComponent = (JPanel) flowTabPane.getSelectedComponent();
        Flow flow;
        if (selectedComponent.equals(structureTab)) {
            FlowTreeNode node = (FlowTreeNode) flowTree.getLastSelectedPathComponent();
            if (null == node || !node.isLeaf()) {
                return null;
            }
            flow = (Flow) node.getUserObject();
        } else {
            flow = flowList.getSelectedValue();
        }
        return flow;
    }

    public JButton getThrottlingButton() {
        return throttlingButton;
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

    public JMenuItem getDnsMenuItem() {
        return dnsMenuItem;
    }

    public JMenuItem getExportRootCertificateMenuItem() {
        return exportRootCertificateMenuItem;
    }

    public JButton getAllButton() {
        return allButton;
    }

    public JButton getJsonFilterButton() {
        return jsonFilterButton;
    }

    public JButton getHtmlFilterButton() {
        return htmlFilterButton;
    }

    public JButton getImageFilterButton() {
        return imageFilterButton;
    }

    public JButton getXmlFilterButton() {
        return xmlFilterButton;
    }

    public JMenuItem getLocalAddressMenuItem() {
        return localAddressMenuItem;
    }
}
