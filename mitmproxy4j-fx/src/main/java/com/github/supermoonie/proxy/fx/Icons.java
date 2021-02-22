package com.github.supermoonie.proxy.fx;

import com.github.supermoonie.proxy.fx.constant.ContentType;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.apache.http.HttpStatus;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 * @author supermoonie
 * @date 2020-11-17
 */
public interface Icons {

    GlyphFont FONT_AWESOME = GlyphFontRegistry.font("FontAwesome");

    Image BLACK_LOADING_ICON = new Image(Icons.class.getResourceAsStream("/icon/loading_000.gif"), 16, 16, false, false);
    Image WHITE_LOADING_ICON = new Image(Icons.class.getResourceAsStream("/icon/loading_fff.gif"), 16, 16, false, false);
    Image CLEAR_ICON = new Image(Icons.class.getResourceAsStream("/icon/clear.png"), 16, 16, false, false);
    Image GRAY_DOT_ICON = new Image(Icons.class.getResourceAsStream("/icon/dot_gray.png"), 16, 16, false, false);
    Image GREEN_DOT_ICON = new Image(Icons.class.getResourceAsStream("/icon/dot_green.png"), 16, 16, false, false);

    /**
     * load icon
     *
     * @param status      response status
     * @param contentType response content-type
     * @return icon
     */
    static Node loadIcon(int status, String contentType) {
        return loadIcon(status, contentType, true);
    }

    /**
     * load icon
     *
     * @param status      response status
     * @param contentType response content-type
     * @param coloring    is color
     * @return icon
     */
    static Node loadIcon(int status, String contentType, boolean coloring) {
        if (status == -1) {
            ImageView imageView = new ImageView(Icons.BLACK_LOADING_ICON);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            return imageView;
        }
        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
            if (null == contentType) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.LINK);
            }
            if (contentType.startsWith(ContentType.TEXT_CSS)) {
                Glyph glyph = Icons.FONT_AWESOME.create(FontAwesome.Glyph.CSS3);
                if (coloring) {
                    Color color = Color.web("#3077b8");
                    glyph.color(color);
                    glyph.setUserData(color);
                }
                return glyph;
            } else if (contentType.startsWith(ContentType.TEXT_XML) || contentType.startsWith(ContentType.APPLICATION_XML)) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith(ContentType.TEXT_PLAIN)) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.FILE_TEXT_ALT);
            } else if (contentType.startsWith(ContentType.APPLICATION_JAVASCRIPT)) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith(ContentType.TEXT_HTML)) {
                Glyph glyph = Icons.FONT_AWESOME.create(FontAwesome.Glyph.HTML5);
                if (coloring) {
                    Color color = Color.web("#d65a26");
                    glyph.color(color);
                    glyph.setUserData(color);
                }
                return glyph;
            } else if (contentType.startsWith(ContentType.APPLICATION_JSON)) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.CODE);
            } else if (contentType.startsWith("image/")) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.PHOTO);
            }
        } else if (status >= HttpStatus.SC_MULTIPLE_CHOICES && status < HttpStatus.SC_BAD_REQUEST) {
            Glyph glyph = Icons.FONT_AWESOME.create(FontAwesome.Glyph.SHARE);
            if (coloring) {
                Color color = Color.web("#f8aa19");
                glyph.color(color);
                glyph.setUserData(color);
            }
            return glyph;
        } else if (status >= HttpStatus.SC_BAD_REQUEST && status < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            return Icons.FONT_AWESOME.create(FontAwesome.Glyph.QUESTION_CIRCLE);
        } else if (status >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            return Icons.FONT_AWESOME.create(FontAwesome.Glyph.BOMB);
        }
        return Icons.FONT_AWESOME.create(FontAwesome.Glyph.LINK);
    }
}
