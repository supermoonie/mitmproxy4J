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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author supermoonie
 * @since 2020/12/20
 */
public class AccessControlRequestIntercept implements RequestIntercept {

    private final Logger log = LoggerFactory.getLogger(AccessControlRequestIntercept.class);

    public static final AccessControlRequestIntercept INSTANCE = new AccessControlRequestIntercept();

    private AccessControlRequestIntercept() {

    }

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        String clientHost = ctx.getConnectionInfo().getClientHost();
        Dao<AccessControl, Integer> accessDao = DaoCollections.getDao(AccessControl.class);
        try {
            long count = accessDao.queryBuilder().where().eq(AccessControl.ACCESS_IP_FIELD_NAME, clientHost).countOf();
            if (count > 0) {
                return null;
            }
        } catch (SQLException e) {
            Application.showError(e);
            return ResponseUtils.htmlResponse(e.getMessage(), HttpResponseStatus.OK);
        }
        String[] options = {"Allow", "Cancel"};
        int i = JOptionPane.showOptionDialog(null, "Allow " + clientHost + " access ? ",
                "Warning!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (0 == i) {
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
            return ResponseUtils.htmlResponse("Forbidden!", HttpResponseStatus.FORBIDDEN);
        }
    }
}
