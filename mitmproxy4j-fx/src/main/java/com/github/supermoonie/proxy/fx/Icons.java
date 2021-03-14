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

    Image WEB_ROOT = new Image(Icons.class.getResourceAsStream("/icon/web_root.png"), 16, 16, false, false);
    Image UPLOAD = new Image(Icons.class.getResourceAsStream("/icon/upload.png"), 16, 16, false, false);
    Image DOWNLOAD = new Image(Icons.class.getResourceAsStream("/icon/download.png"), 16, 16, false, false);
    Image HTML = new Image(Icons.class.getResourceAsStream("/icon/html.png"), 16, 16, false, false);
    Image XML = new Image(Icons.class.getResourceAsStream("/icon/xml.png"), 16, 16, false, false);
    Image TEXT = new Image(Icons.class.getResourceAsStream("/icon/text.png"), 16, 16, false, false);
    Image CSS = new Image(Icons.class.getResourceAsStream("/icon/css.png"), 16, 16, false, false);
    Image JAVASCRIPT = new Image(Icons.class.getResourceAsStream("/icon/js.png"), 16, 16, false, false);
    Image REDIRECT = new Image(Icons.class.getResourceAsStream("/icon/redirect.png"), 16, 16, false, false);
    Image I400 = new Image(Icons.class.getResourceAsStream("/icon/400.png"), 16, 16, false, false);
    Image I500 = new Image(Icons.class.getResourceAsStream("/icon/500.png"), 16, 16, false, false);
    Image BLACK_LOADING_ICON = new Image(Icons.class.getResourceAsStream("/icon/loading_000.gif"), 16, 16, false, false);
    Image WHITE_LOADING_ICON = new Image(Icons.class.getResourceAsStream("/icon/loading_fff.gif"), 16, 16, false, false);
    Image CLEAR_ICON = new Image(Icons.class.getResourceAsStream("/icon/clear.png"), 16, 16, false, false);
    Image GRAY_DOT_ICON = new Image(Icons.class.getResourceAsStream("/icon/dot_gray.png"), 16, 16, false, false);
    Image GREEN_DOT_ICON = new Image(Icons.class.getResourceAsStream("/icon/dot_green.png"), 16, 16, false, false);
    Image JSON = new Image(Icons.class.getResourceAsStream("/icon/json.png"), 16, 16, false, false);

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
            ImageView imageView = new ImageView(Icons.UPLOAD);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            return imageView;
        }
        if (status == 0) {
            ImageView imageView = new ImageView(Icons.DOWNLOAD);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            return imageView;
        }
        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
            if (null == contentType) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.LINK);
            }
            if (contentType.startsWith(ContentType.TEXT_CSS)) {
                ImageView imageView = new ImageView(Icons.CSS);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                return imageView;
            } else if (contentType.startsWith(ContentType.TEXT_XML) || contentType.startsWith(ContentType.APPLICATION_XML)) {
                ImageView imageView = new ImageView(Icons.XML);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                return imageView;
            } else if (contentType.startsWith(ContentType.TEXT_PLAIN)) {
                ImageView imageView = new ImageView(Icons.TEXT);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                return imageView;
            } else if (contentType.contains(ContentType.APPLICATION_JAVASCRIPT)) {
//                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.CODE);
                ImageView imageView = new ImageView(Icons.JAVASCRIPT);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                return imageView;
            } else if (contentType.startsWith(ContentType.TEXT_HTML)) {
                ImageView imageView = new ImageView(Icons.HTML);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                return imageView;
            } else if (contentType.contains(ContentType.APPLICATION_JSON)) {
                ImageView imageView = new ImageView(Icons.JSON);
                imageView.setFitHeight(16);
                imageView.setFitWidth(16);
                return imageView;
            } else if (contentType.startsWith("image/")) {
                return Icons.FONT_AWESOME.create(FontAwesome.Glyph.PHOTO);
            }
        } else if (status >= HttpStatus.SC_MULTIPLE_CHOICES && status < HttpStatus.SC_BAD_REQUEST) {
//            Glyph glyph = Icons.FONT_AWESOME.create(FontAwesome.Glyph.SHARE);
//            if (coloring) {
//                Color color = Color.web("#f8aa19");
//                glyph.color(color);
//                glyph.setUserData(color);
//            }
//            return glyph;
            ImageView imageView = new ImageView(Icons.REDIRECT);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            return imageView;
        } else if (status >= HttpStatus.SC_BAD_REQUEST && status < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
//            return Icons.FONT_AWESOME.create(FontAwesome.Glyph.QUESTION_CIRCLE);
            ImageView imageView = new ImageView(Icons.I400);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            return imageView;
        } else if (status >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
//            return Icons.FONT_AWESOME.create(FontAwesome.Glyph.BOMB);
            ImageView imageView = new ImageView(Icons.I500);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            return imageView;
        }
        return Icons.FONT_AWESOME.create(FontAwesome.Glyph.LINK);
    }
}
