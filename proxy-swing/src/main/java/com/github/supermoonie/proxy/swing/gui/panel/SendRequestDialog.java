package com.github.supermoonie.proxy.swing.gui.panel;

import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.gui.table.FormDataTable;
import com.github.supermoonie.proxy.swing.gui.table.NameValueTable;
import com.github.supermoonie.proxy.swing.mime.MimeMappings;
import com.github.supermoonie.proxy.swing.setting.GlobalSetting;
import com.github.supermoonie.proxy.swing.util.HttpClientUtil;
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
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/12/6
 */
public class SendRequestDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(SendRequestDialog.class);

    /**
     * method + url
     */
    private final JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"GET", "POST", "HEAD", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"});
    private final JTextField urlTextField = new JTextField("https://");

    /**
     * send & cancel button
     */
    private final JCheckBox closeWindowCheckBox = new JCheckBox("Close window after send");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton sendButton = new JButton("Send");

    /**
     * query table
     */
    private final JTable queryTable = new NameValueTable();

    /**
     * header table
     */
    private final JTable headerTable = new NameValueTable();

    /**
     * body component
     */
    private final JRadioButton noneRadioButton = new JRadioButton("none");
    private final JRadioButton formDataRadioButton = new JRadioButton("form-data");
    private final JRadioButton urlEncodedRadioButton = new JRadioButton("x-www-form-urlencoded");
    private final JRadioButton rawRadioButton = new JRadioButton("raw");
    private final JRadioButton binaryRadioButton = new JRadioButton("binary");
    private final JComboBox<String> rawTypeComboBox = new JComboBox<>(new String[]{"Text", "JavaScript", "JSON", "HTML", "XML"});
    private final JPanel nonePanel = new JPanel(new GridBagLayout());
    private final JTable urlEncodedTable = new NameValueTable();
    private final JTable formDataTable = new FormDataTable();
    private final RSyntaxTextArea rawCodeArea = new RSyntaxTextArea();
    private final JLabel binaryLabel = new JLabel();
    private File binaryFile;

    public SendRequestDialog(Frame owner, String title, boolean modal) {
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
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(closeWindowCheckBox);
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(sendButton);
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(cancelButton);
        super.add(southPanel, BorderLayout.SOUTH);
        cancelButton.addActionListener(e -> setVisible(false));
        sendButton.addActionListener(e -> onSendButtonClicked());

        super.setPreferredSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    private void onSendButtonClicked() {
        try (CloseableHttpClient httpClient = HttpClientUtil.createTrustAllApacheHttpClientBuilder()
                .setProxy(new HttpHost("127.0.0.1", GlobalSetting.getInstance().getPort()))
                .build()) {
            String method = Objects.requireNonNullElse(methodComboBox.getSelectedItem(), "GET").toString().toLowerCase();
            String url = urlTextField.getText();
            RequestBuilder requestBuilder = RequestBuilder.create(method).setUri(url);
            int headerRowCount = headerTable.getModel().getRowCount();
            for (int row = 0; row < headerRowCount; row++) {
                String name = headerTable.getModel().getValueAt(row, 0).toString();
                String value = headerTable.getModel().getValueAt(row, 1).toString();
                if ("".equals(name) && "".equals(value)) {
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
                    if ("".equals(name) && "".equals(value) && "".equals(path)) {
                        continue;
                    }
                    if (!"".equals(path)) {
                        File file = new File(path);
                        String mimeType = formDataTable.getModel().getValueAt(row, 3).toString();
                        if ("Auto".equals(mimeType)) {
                            mimeType = MimeMappings.DEFAULT.get(file.getName().substring(file.getName().lastIndexOf(".") + 1));
                        }
                        entityBuilder.addBinaryBody(name, file, ContentType.getByMimeType(mimeType), file.getName());
                    } else {
                        entityBuilder.addTextBody(name, value, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
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
            System.out.println("method: " + method);
            System.out.println("url: " + url);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

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
        nonePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.gridColor")));
        JScrollPane urlEncodedScrollPanel = new JScrollPane(urlEncodedTable);
        JScrollPane formDataScrollPanel = new JScrollPane(formDataTable);
        rawCodeArea.setCodeFoldingEnabled(false);
        rawCodeArea.setEditable(true);
        rawCodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        if (ThemeManager.isDark()) {
            ThemeManager.getCodeAreaDarkTheme().apply(rawCodeArea);
        } else {
            ThemeManager.getCodeAreaLightTheme().apply(rawCodeArea);
        }
        RTextScrollPane codeScrollPane = new RTextScrollPane(rawCodeArea);
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
        binaryPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.gridColor")));
        JButton selectButton = new JButton("Select...");
        selectButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            if (null != binaryFile) {
                chooser.setSelectedFile(binaryFile);
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(SendRequestDialog.this)) {
                binaryFile = chooser.getSelectedFile();
                binaryLabel.setText(binaryFile.getAbsolutePath());
                binaryPanel.add(Box.createHorizontalStrut(10));
            }
        });
        binaryPanel.add(selectButton);
        binaryPanel.add(Box.createHorizontalStrut(10));
        binaryPanel.add(binaryLabel);
        JPanel centerPanel = new JPanel(new BorderLayout());
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
