package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Content;
import com.github.supermoonie.proxy.swing.entity.Header;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.gui.MainFrameHelper;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.gui.table.CurdTable;
import com.github.supermoonie.proxy.swing.gui.table.FormDataTable;
import com.github.supermoonie.proxy.swing.gui.table.NameValueTable;
import com.github.supermoonie.proxy.swing.mime.MimeMappings;
import com.github.supermoonie.proxy.swing.setting.GlobalSetting;
import com.github.supermoonie.proxy.swing.util.HttpClientUtil;
import com.j256.ormlite.dao.Dao;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/12/6
 */
public class ComposeDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(ComposeDialog.class);

    /**
     * method + url
     */
    private final JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"GET", "POST", "HEAD", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"});
    private final JTextField urlTextField = new JTextField("https://");

    private final JCheckBox closeWindowCheckBox = new JCheckBox("Close window after send");
    private final JButton sendButton = new JButton("Send");

    /**
     * query table
     */
    private final NameValueTable queryTable = new NameValueTable();

    /**
     * header table
     */
    private final NameValueTable headerTable = new NameValueTable();

    /**
     * body component
     */
    private final JPanel centerPanel = new JPanel(new BorderLayout());
    private final JRadioButton noneRadioButton = new JRadioButton("none");
    private final JRadioButton formDataRadioButton = new JRadioButton("form-data");
    private final JRadioButton urlEncodedRadioButton = new JRadioButton("x-www-form-urlencoded");
    private final JRadioButton rawRadioButton = new JRadioButton("raw");
    private final JRadioButton binaryRadioButton = new JRadioButton("binary");
    private final JComboBox<String> rawTypeComboBox = new JComboBox<>(new String[]{"Text", "JavaScript", "JSON", "HTML", "XML"});
    private final JPanel nonePanel = new JPanel(new GridBagLayout());
    private JScrollPane urlEncodedScrollPanel;
    private final NameValueTable urlEncodedTable = new NameValueTable();
    private final FormDataTable formDataTable = new FormDataTable();
    private RTextScrollPane codeScrollPane;
    private final RSyntaxTextArea rawCodeArea = new RSyntaxTextArea();
    private final JLabel binaryLabel = new JLabel();
    private File binaryFile;

    public ComposeDialog(Frame owner, String title, Flow flow, boolean modal) {
        super(owner, title, modal);
        super.setLayout(new BorderLayout());
        // north panel
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        methodComboBox.setEditable(false);
        urlTextField.requestFocus();
        northPanel.add(methodComboBox, BorderLayout.WEST);
        northPanel.add(urlTextField, BorderLayout.CENTER);
        super.add(northPanel, BorderLayout.NORTH);
        // center panel
        JTabbedPane centerPane = initCenterPanel();
        super.add(centerPane, BorderLayout.CENTER);
        // south panel
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(closeWindowCheckBox);
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(sendButton);
        southPanel.add(Box.createHorizontalStrut(10));
        JButton cancelButton = new JButton("Cancel");
        southPanel.add(cancelButton);
        super.add(southPanel, BorderLayout.SOUTH);
        cancelButton.addActionListener(e -> setVisible(false));
        sendButton.addActionListener(e -> onSendButtonClicked());

        fillFlow(flow);

        ((DefaultTableModel) queryTable.getModel()).addRow(new Object[]{"", "", "Del"});
        ((DefaultTableModel) headerTable.getModel()).addRow(new Object[]{"", "", "Del"});
        ((DefaultTableModel) urlEncodedTable.getModel()).addRow(new Object[]{"", "", "Del"});
        queryTable.setRowHeight(25);
        headerTable.setRowHeight(25);
        urlEncodedTable.setRowHeight(25);
        formDataTable.setRowHeight(25);
        urlTextField.getDocument().addDocumentListener(urlTextFieldDocumentListener);
        queryTable.getModel().addTableModelListener(queryTableModelListener);
        queryTable.getButtonEditor().addActionListener(e -> {
            urlTextField.getDocument().removeDocumentListener(urlTextFieldDocumentListener);
            queryTable.getButtonEditor().fireEditingStopped();
            int selectedRow = queryTable.getSelectedRow();
            if (-1 == selectedRow) {
                return;
            }
            ((DefaultTableModel) queryTable.getModel()).removeRow(selectedRow);
            queryTable.clearSelection();
            queryTable.addRow();
            urlTextField.getDocument().addDocumentListener(urlTextFieldDocumentListener);
        });
        headerTable.getButtonEditor().addActionListener(e -> {
            headerTable.getButtonEditor().fireEditingStopped();
            int selectedRow = headerTable.getSelectedRow();
            if (-1 == selectedRow) {
                return;
            }
            ((DefaultTableModel) headerTable.getModel()).removeRow(selectedRow);
            headerTable.clearSelection();
            headerTable.addRow();
        });
        urlEncodedTable.getButtonEditor().addActionListener(e -> {
            urlEncodedTable.getButtonEditor().fireEditingStopped();
            int selectedRow = urlEncodedTable.getSelectedRow();
            if (-1 == selectedRow) {
                return;
            }
            ((DefaultTableModel) urlEncodedTable.getModel()).removeRow(selectedRow);
            urlEncodedTable.clearSelection();
            urlEncodedTable.addRow();
        });
        formDataTable.getButtonEditor().addActionListener(e -> {
            formDataTable.getButtonEditor().fireEditingStopped();
            int selectedRow = formDataTable.getSelectedRow();
            if (-1 == selectedRow) {
                return;
            }
            ((DefaultTableModel) formDataTable.getModel()).removeRow(selectedRow);
            formDataTable.clearSelection();
            formDataTable.addRow();
        });
        headerTable.getModel().addTableModelListener(e -> {
            if ((e.getLastRow() + 1) == headerTable.getModel().getRowCount()) {
                headerTable.addRow();
            }
        });
        urlEncodedTable.getModel().addTableModelListener(e -> {
            if ((e.getLastRow() + 1) == urlEncodedTable.getModel().getRowCount()) {
                urlEncodedTable.addRow();
            }
        });
        closeWindowCheckBox.setSelected(ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_CLOSE_AFTER_SEND, false));
        closeWindowCheckBox.addActionListener(e -> ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_CLOSE_AFTER_SEND, closeWindowCheckBox.isSelected()));

        super.setFocusable(true);
        super.setPreferredSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    private final TableModelListener queryTableModelListener = e -> {
        if ((e.getLastRow() + 1) == queryTable.getModel().getRowCount()) {
            queryTable.addRow();
        }
        StringBuilder urlBuilder = new StringBuilder();
        String url = urlTextField.getText();
        if (null != url && !"".equals(url)) {
            if (url.contains("?")) {
                urlBuilder.append(url, 0, url.indexOf("?") + 1);
            } else {
                urlBuilder.append(url).append("?");
            }
        }
        DefaultTableModel queryModel = (DefaultTableModel) queryTable.getModel();
        int rowCount = queryModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String name = Objects.requireNonNullElse(queryModel.getValueAt(i, 0), "").toString();
            String value = Objects.requireNonNullElse(queryModel.getValueAt(i, 1), "").toString();
            if ("".equals(name) && "".equals(value)) {
                continue;
            }
            urlBuilder.append(name).append("=").append(value).append("&");
        }
        urlTextField.setText(urlBuilder.substring(0, urlBuilder.length() - 1));
    };

    private final DocumentListener urlTextFieldDocumentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            onChange();
        }

        public void onChange() {
            try {
                URI uri = new URI(urlTextField.getText());
                String query = uri.getQuery();
                if (null == query) {
                    return;
                }
                List<String[]> queryList = MainFrameHelper.splitQuery(query);
                DefaultTableModel queryModel = (DefaultTableModel) queryTable.getModel();
                queryModel.removeTableModelListener(queryTableModelListener);
                int rowCount = queryModel.getRowCount();
                for (int i = rowCount - 1; i >= 0; i--) {
                    queryModel.removeRow(i);
                }
                queryModel.fireTableDataChanged();
                for (String[] queryArr : queryList) {
                    queryModel.addRow(new String[]{queryArr[0], queryArr[1], "Del"});
                }
                queryModel.addRow(new String[]{"", "", "Del"});
                queryModel.addTableModelListener(queryTableModelListener);
            } catch (URISyntaxException ignore) {

            }
        }

    };

    private void fillFlow(Flow flow) {
        if (null != flow) {
            try {
                Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
                Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
                Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
                Request request = requestDao.queryForId(flow.getRequestId());
                methodComboBox.setSelectedItem(request.getMethod().toUpperCase());
                urlTextField.setText(request.getUri());
                URI uri = new URI(request.getUri());
                String query = uri.getQuery();
                if (null != query) {
                    List<String[]> queryList = MainFrameHelper.splitQuery(query);
                    DefaultTableModel model = ((DefaultTableModel) queryTable.getModel());
                    for (String[] queryArr : queryList) {
                        model.addRow(new String[]{queryArr[0], queryArr[1], "Del"});
                    }
                }
                List<Header> headers = headerDao.queryBuilder().where()
                        .eq(Header.REQUEST_ID_FIELD_NAME, request.getId())
                        .and().isNull(Header.RESPONSE_ID_FIELD_NAME)
                        .query();
                DefaultTableModel model = (DefaultTableModel) headerTable.getModel();
                for (Header header : headers) {
                    model.addRow(new String[]{header.getName(), header.getValue(), "Del"});
                }
                Content content = contentDao.queryForId(request.getContentId());
                headers.stream().filter(header -> header.getName().toLowerCase().equals("content-type")).findFirst().ifPresent(header -> {
                    if (null != content && null != content.getRawContent() && null != header.getValue()) {
                        String headerValue = header.getValue().toLowerCase();
                        if (headerValue.contains("x-www-form-urlencoded")) {
                            DefaultTableModel urlEncodedModel = (DefaultTableModel) urlEncodedTable.getModel();
                            String urlencoded = URLDecoder.decode(new String(content.getRawContent(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                            List<String[]> urlencodedList = MainFrameHelper.splitQuery(urlencoded);
                            for (String[] urlencodedArr : urlencodedList) {
                                urlEncodedModel.addRow(new String[]{urlencodedArr[0], urlencodedArr[1], "Del"});
                            }
                            urlEncodedRadioButton.setSelected(true);
                            urlEncodedRadioButton.doClick();
                        } else if (headerValue.contains("text/plain")) {
                            rawCodeArea.setText(new String(content.getRawContent(), StandardCharsets.UTF_8));
                            rawCodeArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_NONE);
                            rawTypeComboBox.setSelectedItem("Text");
                            rawRadioButton.setSelected(true);
                            centerPanel.removeAll();
                            centerPanel.add(codeScrollPane);
                        } else if (headerValue.contains("application/javascript")) {
                            rawCodeArea.setText(new String(content.getRawContent(), StandardCharsets.UTF_8));
                            rawCodeArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVASCRIPT);
                            rawTypeComboBox.setSelectedItem("JavaScript");
                            rawRadioButton.setSelected(true);
                            centerPanel.removeAll();
                            centerPanel.add(codeScrollPane);
                        } else if (headerValue.contains("application/json")) {
                            rawCodeArea.setText(new String(content.getRawContent(), StandardCharsets.UTF_8));
                            rawCodeArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);
                            rawTypeComboBox.setSelectedItem("JSON");
                            rawRadioButton.setSelected(true);
                            centerPanel.removeAll();
                            centerPanel.add(codeScrollPane);
                        } else if (headerValue.contains("text/html")) {
                            rawCodeArea.setText(new String(content.getRawContent(), StandardCharsets.UTF_8));
                            rawCodeArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_HTML);
                            rawTypeComboBox.setSelectedItem("HTML");
                            rawRadioButton.setSelected(true);
                            centerPanel.removeAll();
                            centerPanel.add(codeScrollPane);
                        } else if (headerValue.contains("application/xml")) {
                            rawCodeArea.setText(new String(content.getRawContent(), StandardCharsets.UTF_8));
                            rawCodeArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_XML);
                            rawTypeComboBox.setSelectedItem("XML");
                            rawRadioButton.setSelected(true);
                            centerPanel.removeAll();
                            centerPanel.add(codeScrollPane);
                        }
                    }
                });
            } catch (SQLException | URISyntaxException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void onSendButtonClicked() {
        sendButton.setText("Sending...");
        sendButton.setEnabled(false);
        Application.EXECUTOR.execute(() -> {
            try (CloseableHttpClient httpClient = HttpClientUtil.createTrustAllApacheHttpClientBuilder()
                    .setProxy(new HttpHost("127.0.0.1", GlobalSetting.getInstance().getPort()))
                    .build()) {
                String method = Objects.requireNonNullElse(methodComboBox.getSelectedItem(), "GET").toString();
                String url = urlTextField.getText();
                RequestBuilder requestBuilder = RequestBuilder.create(method).setUri(url);
                int headerRowCount = headerTable.getModel().getRowCount();
                for (int row = 0; row < headerRowCount; row++) {
                    String name = headerTable.getModel().getValueAt(row, 0).toString();
                    String value = headerTable.getModel().getValueAt(row, 1).toString();
                    if ("".equals(name) && "".equals(value)) {
                        continue;
                    }
                    if (name.toLowerCase().equals("content-length")) {
                        continue;
                    }
                    requestBuilder.addHeader(name, value);
                }
                if (formDataRadioButton.isSelected()) {
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    int formDataRowCount = formDataTable.getModel().getRowCount();
                    for (int row = 0; row < formDataRowCount; row++) {
                        String name = formDataTable.getModel().getValueAt(row, 0).toString();
                        String value = formDataTable.getModel().getValueAt(row, 1).toString();
                        String path = formDataTable.getModel().getValueAt(row, 2).toString();
                        if ("".equals(name) && "".equals(value) && ("".equals(path) || "Select...".equals(path))) {
                            continue;
                        }
                        if (!"".equals(value)) {
                            entityBuilder.addTextBody(name, value, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
                        } else {
                            File file = new File(path);
                            String mimeType = formDataTable.getModel().getValueAt(row, 3).toString();
                            if ("Auto".equals(mimeType)) {
                                mimeType = MimeMappings.DEFAULT.get(file.getName().substring(file.getName().lastIndexOf(".") + 1));
                            }
                            entityBuilder.addBinaryBody(name, file, ContentType.getByMimeType(mimeType), file.getName());
                        }
                    }
                    requestBuilder.setEntity(entityBuilder.build());
                } else if (urlEncodedRadioButton.isSelected()) {
                    StringBuilder params = new StringBuilder();
                    int rowCount = urlEncodedTable.getModel().getRowCount();
                    for (int row = 0; row < rowCount; row++) {
                        String name = urlEncodedTable.getModel().getValueAt(row, 0).toString();
                        String value = urlEncodedTable.getModel().getValueAt(row, 1).toString();
                        if ("".equals(name) && "".equals(value)) {
                            continue;
                        }
                        params.append(name).append("=").append(value).append("&");
                    }
                    StringEntity entity = new StringEntity(params.toString(), ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8));
                    requestBuilder.setEntity(entity);
                } else if (rawRadioButton.isSelected()) {
                    String raw = rawCodeArea.getText();
                    String type = Objects.requireNonNullElse(rawTypeComboBox.getSelectedItem(), "Text").toString();
                    if ("JavaScript".equals(type)) {
                        requestBuilder.setEntity(new StringEntity(raw, ContentType.create("application/javascript", StandardCharsets.UTF_8)));
                    } else if ("JSON".equals(type)) {
                        requestBuilder.setEntity(new StringEntity(raw, ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));
                    } else if ("HTML".equals(type)) {
                        requestBuilder.setEntity(new StringEntity(raw, ContentType.TEXT_HTML.withCharset(StandardCharsets.UTF_8)));
                    } else if ("XML".equals(type)) {
                        requestBuilder.setEntity(new StringEntity(raw, ContentType.APPLICATION_XML.withCharset(StandardCharsets.UTF_8)));
                    } else {
                        requestBuilder.setEntity(new StringEntity(raw, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8)));
                    }
                } else if (binaryRadioButton.isSelected()) {
                    if (null != binaryFile) {
                        requestBuilder.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.DEFAULT_BINARY.toString());
                        requestBuilder.setEntity(new FileEntity(binaryFile));
                    }
                }
                httpClient.execute(requestBuilder.build()).close();
                if (closeWindowCheckBox.isSelected()) {
                    setVisible(false);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                SwingUtilities.invokeLater(() -> {
                    sendButton.setText("Send");
                    sendButton.setEnabled(true);
                });
            }
        });

    }

    private JTabbedPane initCenterPanel() {
        JTabbedPane centerPane = new JTabbedPane();
        JScrollPane queryScrollPane = new JScrollPane(queryTable);
        centerPane.add("Query", queryScrollPane);
        JScrollPane headerScrollPane = new JScrollPane(headerTable);
        centerPane.add("Header", headerScrollPane);
        JPanel bodyPanel = initBodyPanel();
        centerPane.add("Body", bodyPanel);
        return centerPane;
    }

    private JPanel initBodyPanel() {
        JPanel bodyPanel = new JPanel(new BorderLayout());
        // radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        noneRadioButton.setSelected(true);
        rawTypeComboBox.setEnabled(false);
        rawTypeComboBox.setMaximumSize(new Dimension(100, 23));
        rawRadioButton.addChangeListener(e -> rawTypeComboBox.setEnabled(rawRadioButton.isSelected()));
        radioButtonGroup.add(noneRadioButton);
        radioButtonGroup.add(formDataRadioButton);
        radioButtonGroup.add(urlEncodedRadioButton);
        radioButtonGroup.add(rawRadioButton);
        radioButtonGroup.add(binaryRadioButton);
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(noneRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(formDataRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(urlEncodedRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(rawRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(binaryRadioButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(rawTypeComboBox);
        northPanel.add(buttonPanel, BorderLayout.CENTER);
        northPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
        bodyPanel.add(northPanel, BorderLayout.NORTH);
        // body content
        nonePanel.add(new JLabel("This request does not have a body"));
        nonePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.borderColor")));
        urlEncodedScrollPanel = new JScrollPane(urlEncodedTable);
        JScrollPane formDataScrollPanel = new JScrollPane(formDataTable);
        rawCodeArea.setCodeFoldingEnabled(false);
        rawCodeArea.setEditable(true);
        rawCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        if (ThemeManager.isDark()) {
            ThemeManager.getCodeAreaDarkTheme().apply(rawCodeArea);
        } else {
            ThemeManager.getCodeAreaLightTheme().apply(rawCodeArea);
        }
        codeScrollPane = new RTextScrollPane(rawCodeArea);
        rawTypeComboBox.addItemListener(e -> {
            String type = Objects.requireNonNullElse(rawTypeComboBox.getSelectedItem(), "Text").toString();
            switch (type) {
                case "Text":
                    rawCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                    break;
                case "JavaScript":
                    rawCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                    break;
                case "JSON":
                    rawCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
                    break;
                case "HTML":
                    rawCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
                    break;
                case "XML":
                    rawCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                    break;
            }
        });
        JPanel binaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        binaryPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.borderColor")));
        JButton selectButton = new JButton("Select...");
        selectButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            if (null != binaryFile) {
                chooser.setSelectedFile(binaryFile);
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(ComposeDialog.this)) {
                binaryFile = chooser.getSelectedFile();
                binaryLabel.setText(binaryFile.getAbsolutePath());
                binaryPanel.add(Box.createHorizontalStrut(10));
            }
        });
        binaryPanel.add(selectButton);
        binaryPanel.add(Box.createHorizontalStrut(10));
        binaryPanel.add(binaryLabel);
        centerPanel.add(nonePanel);
        bodyPanel.add(centerPanel, BorderLayout.CENTER);
        // action listener
        noneRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(nonePanel);
            centerPanel.updateUI();
        });
        urlEncodedRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(urlEncodedScrollPanel);
            centerPanel.updateUI();
        });
        formDataRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(formDataScrollPanel);
            centerPanel.updateUI();
        });
        rawRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(codeScrollPane);
            centerPanel.updateUI();
        });
        binaryRadioButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(binaryPanel);
            centerPanel.updateUI();
        });
        return bodyPanel;
    }
}
