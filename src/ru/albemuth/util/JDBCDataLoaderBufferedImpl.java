package ru.albemuth.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public abstract class JDBCDataLoaderBufferedImpl<T> extends DataLoader<T> {

    private Connection conn;
    private Iterator<T> dataIterator;

    public void configure(Configuration cfg) throws ConfigurationException {
        try {
            Class.forName(cfg.getStringValue(this, "jdbc-driver-classname"));
            Properties connInfo = new Properties();

            connInfo.put("user", cfg.getStringValue(this, "username"));
            connInfo.put("password", cfg.getStringValue(this, "password"));

            this.conn = DriverManager.getConnection(cfg.getStringValue(this, "dburl"), connInfo);

            PreparedStatement st = conn.prepareStatement(getDataLoadSQLQuery(cfg));

            ResultSet rs = st.executeQuery();

            List<T> data = new ArrayList<T>();
            for (; rs.next(); ) {
                data.add(createInstance(rs));
            }
            rs.close();
            st.close();

            dataIterator = data.iterator();
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("JDBC driver class not found", e);
        } catch (SQLException e) {
            throw new ConfigurationException("Can't configure url source", e);
        }

        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws CloseException {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new CloseException("Can't close jdbc connection", e);
            }
        }
    }

    protected String getDataLoadSQLQuery(Configuration cfg) throws ConfigurationException {
        return cfg.getStringValue(this, "data-load-sql-query");
    }

    public abstract T createInstance(ResultSet rs) throws SQLException;

    @Override
    public T next() throws LoadException {
        return dataIterator.hasNext() ? dataIterator.next() : null;
    }

}
