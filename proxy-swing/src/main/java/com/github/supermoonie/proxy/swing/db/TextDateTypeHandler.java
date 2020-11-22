package com.github.supermoonie.proxy.swing.db;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-11-22
 */
public class TextDateTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Date date, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public Date getNullableResult(ResultSet resultSet, String s) throws SQLException {
        try {
            String timeCreated = resultSet.getString(s);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeCreated);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Date getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    @Override
    public Date getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }
}
