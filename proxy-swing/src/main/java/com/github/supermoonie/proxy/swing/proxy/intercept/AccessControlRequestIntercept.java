package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AccessControl;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.github.supermoonie.proxy.util.ResponseUtils;
import com.j256.ormlite.dao.Dao;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author supermoonie
 * @since 2020/12/20
 */
public class AccessControlRequestIntercept implements RequestIntercept {

    public static final AccessControlRequestIntercept INSTANCE = new AccessControlRequestIntercept();

    private static final Set<String> NOT_ALLOW_SET = new HashSet<>();

    private AccessControlRequestIntercept() {

    }

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        Set<String> set = ApplicationPreferences.getAccessControl();
        String clientHost = ctx.getConnectionInfo().getClientHost();
        if (set.contains(clientHost)) {
            return null;
        }
        if (!NOT_ALLOW_SET.contains(clientHost)) {
            String[] options = {"Allow", "Cancel"};
            int i = JOptionPane.showOptionDialog(null, "Allow " + clientHost + " access ? ",
                    "Warning!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (0 == i) {
                ApplicationPreferences.getAccessControl().add(clientHost);
                Dao<AccessControl, Integer> accessControlDao = DaoCollections.getDao(AccessControl.class);
                AccessControl ac = new AccessControl();
                ac.setAccessIp(clientHost);
                ac.setTimeCreated(new Date());
                try {
                    accessControlDao.create(ac);
                } catch (SQLException t) {
                    Application.showError(t);
                }
                return null;
            } else {
                NOT_ALLOW_SET.add(clientHost);
                return ResponseUtils.htmlResponse("Forbidden!", HttpResponseStatus.FORBIDDEN);
            }
        }
        return ResponseUtils.htmlResponse("Forbidden!", HttpResponseStatus.FORBIDDEN);
    }
}
