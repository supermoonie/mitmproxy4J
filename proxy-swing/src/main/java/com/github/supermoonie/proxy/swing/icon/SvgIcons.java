package com.github.supermoonie.proxy.swing.icon;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.entity.Response;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.swing.*;

/**
 * @author supermoonie
 * @since 2020/11/26
 */
public interface SvgIcons {

    FlatSVGIcon LIGHTING = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/lighting.svg");
    FlatSVGIcon REPEAT = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/repeat.svg");
    Icon HTML = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/html.svg", Application.class.getClassLoader());
    FlatSVGIcon JSON = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/json.svg", Application.class.getClassLoader());
    FlatSVGIcon JAVA_SCRIPT = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/js.svg");
    FlatSVGIcon CSS = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/css.svg");
    FlatSVGIcon PLAIN_TEXT = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/text.svg");
    FlatSVGIcon XML = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/xml.svg");
    FlatSVGIcon IMAGE = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/image.svg");
    FlatSVGIcon ANY_TYPE = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/any_type.svg");
    FlatSVGIcon REDIRECT = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/redirect.svg");
    FlatSVGIcon QUESTION = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/400.svg");
    FlatSVGIcon BOMB = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/bomb.svg");
    FlatSVGIcon TREE = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/tree.svg");
    FlatSVGIcon LIST = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/list.svg");
    FlatSVGIcon UPLOAD = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/upload.svg");
    FlatSVGIcon DOWNLOAD = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/download.svg");
    FlatSVGIcon WEB_ROOT = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/web_root.svg");
    FlatSVGIcon LEAF = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/leaf.svg");
    FlatSVGIcon BRANCH = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/branch.svg");
    FlatSVGIcon CLEAR = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/clear.svg");
    FlatSVGIcon PLAY = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/play.svg");
    FlatSVGIcon STOP = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/stop.svg");
    FlatSVGIcon THROTTLING = new FlatSVGIcon("com/github/supermoonie/proxy/swing/icon/throttling_stop.svg");

    static Icon loadIcon(int status, String contentType) {
        if (null == contentType) {
            return SvgIcons.ANY_TYPE;
        }
        if (status >= HttpResponseStatus.OK.code() && status < HttpResponseStatus.MULTIPLE_CHOICES.code()) {
            contentType = contentType.toLowerCase();
            if (contentType.contains("css")) {
                return SvgIcons.CSS;
            } else if (contentType.contains("xml")) {
                return SvgIcons.XML;
            } else if (contentType.contains("plain")) {
                return SvgIcons.PLAIN_TEXT;
            } else if (contentType.contains("javascript")) {
                return SvgIcons.JAVA_SCRIPT;
            } else if (contentType.contains("html")) {
                return SvgIcons.HTML;
            } else if (contentType.contains("json")) {
                return SvgIcons.JSON;
            } else if (contentType.startsWith("image/")) {
                return SvgIcons.IMAGE;
            }
        } else if (status >= HttpResponseStatus.MULTIPLE_CHOICES.code() && status < HttpResponseStatus.BAD_REQUEST.code()) {
            return SvgIcons.REDIRECT;
        } else if (status >= HttpResponseStatus.BAD_REQUEST.code() && status < HttpResponseStatus.INTERNAL_SERVER_ERROR.code()) {
            return SvgIcons.QUESTION;
        } else if (status >= HttpResponseStatus.INTERNAL_SERVER_ERROR.code()) {
            return SvgIcons.BOMB;
        }
        return SvgIcons.ANY_TYPE;
    }
}
