package com.github.supermoonie.proxy.swing.gui.lintener;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Content;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.gui.MainFrame;
import com.github.supermoonie.proxy.swing.gui.MainFrameHelper;
import com.github.supermoonie.proxy.swing.gui.flow.Flow;
import com.github.supermoonie.proxy.swing.prettify.JavascriptBeautifierForJava;
import com.j256.ormlite.dao.Dao;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prettify.PrettifyParser;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;

import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author supermoonie
 * @since 2020/12/1
 */
public class ResponseCodeAreaShownListener extends ComponentAdapter {

    private final Logger log = LoggerFactory.getLogger(ResponseCodeAreaShownListener.class);

    private static volatile int SHOWING_RESPONSE_ID = -1;

    private final JProgressBar processBar = new JProgressBar(SwingConstants.HORIZONTAL);

    @Override
    public void componentShown(ComponentEvent e) {
        MainFrame mainFrame = Application.MAIN_FRAME;
        RSyntaxTextArea responseCodeArea = mainFrame.getResponseCodeArea();
        Flow flow = MainFrameHelper.getSelectedFlow();
        if (null == flow || null == flow.getResponseId() || null == flow.getContentType()) {
            responseCodeArea.setText(null);
            log.info("flow is null");
            return;
        }
        if (flow.getResponseId().equals(SHOWING_RESPONSE_ID)) {
            log.info("responseId: {} is showing", flow.getRequestId());
            return;
        }
        SHOWING_RESPONSE_ID = flow.getResponseId();
        responseCodeArea.setText("");
        try {
            String contentType = flow.getContentType().toLowerCase();
            Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
            Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
            Response response = responseDao.queryForId(flow.getResponseId());
            if (null == response.getContentId()) {
                return;
            }
            Content content = contentDao.queryForId(response.getContentId());
            if (contentType.contains("html")) {
                prettifyAndShow(SyntaxConstants.SYNTAX_STYLE_HTML, content);
            } else if (contentType.contains("json")) {
                prettifyAndShow(SyntaxConstants.SYNTAX_STYLE_JSON, content);
            } else if (contentType.contains("javascript")) {
                prettifyAndShow(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, content);
            } else if (contentType.contains("css")) {
                prettifyAndShow(SyntaxConstants.SYNTAX_STYLE_CSS, content);
            } else if (contentType.contains("xml")) {
                prettifyAndShow(SyntaxConstants.SYNTAX_STYLE_XML, content);
            } else if (contentType.contains("text") || contentType.contains("txt")) {
                prettifyAndShow(SyntaxConstants.SYNTAX_STYLE_NONE, content);
            }
        } catch (SQLException ex) {
            Application.showError(ex);
        }
    }

    private void prettifyAndShow(String style, Content content) {
        MainFrame mainFrame = Application.MAIN_FRAME;
        JPanel responseCodePane = mainFrame.getResponseCodePane();
        RSyntaxTextArea responseCodeArea = mainFrame.getResponseCodeArea();
        responseCodePane.removeAll();
        processBar.setValue(0);
        processBar.setIndeterminate(true);
        responseCodePane.add(processBar, BorderLayout.SOUTH);
        Application.EXECUTOR.execute(() -> {
            String body = new String(content.getRawContent(), StandardCharsets.UTF_8);
            final AtomicReference<String> codeText = new AtomicReference<>();
            switch (style) {
                case SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT:
                case SyntaxConstants.SYNTAX_STYLE_JSON:
                    try {
                        codeText.set(JavascriptBeautifierForJava.INSTANCE.beautifyJavascriptCode(body));
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error(ex.getLocalizedMessage(), ex);
                    }
                    break;
                case SyntaxConstants.SYNTAX_STYLE_CSS:
                    try {
                        codeText.set(JavascriptBeautifierForJava.INSTANCE.beautifyCssCode(body));
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error(ex.getLocalizedMessage(), ex);
                    }
                    break;
                case SyntaxConstants.SYNTAX_STYLE_HTML:
                case SyntaxConstants.SYNTAX_STYLE_XML:
                    try {
                        codeText.set(JavascriptBeautifierForJava.INSTANCE.beautifyHtmlCode(body));
                    } catch (ScriptException | NoSuchMethodException ex) {
                        log.error(ex.getLocalizedMessage(), ex);
                    }
                    break;
                default:
//                String prettify = prettify(style.replace("text/", ""), body);
                    codeText.set(body);
                    break;
            }
            SwingUtilities.invokeLater(() -> {
                responseCodePane.add(new RTextScrollPane(responseCodeArea), BorderLayout.CENTER);
                responseCodePane.remove(processBar);
                responseCodePane.updateUI();
                responseCodeArea.setSyntaxEditingStyle(style);
                responseCodeArea.setText(codeText.get());
                responseCodeArea.setFont(ApplicationPreferences.getFont());
                responseCodeArea.setCaretPosition(0);
            });
        });
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        super.componentHidden(e);
    }

    private String prettify(String extension, String content) {
        Parser parser = new PrettifyParser();
        List<ParseResult> results = parser.parse(extension, content);
        StringBuilder sb = new StringBuilder();
        for (ParseResult result : results) {
            sb.append(content, result.getOffset(), result.getOffset() + result.getLength());
        }
        return sb.toString();
    }
}
