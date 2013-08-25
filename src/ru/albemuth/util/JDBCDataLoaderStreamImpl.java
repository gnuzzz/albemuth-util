package ru.albemuth.util;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

public abstract class JDBCDataLoaderStreamImpl<T> extends DataLoader<T> {

    private static final Logger LOG                 = Logger.getLogger(JDBCDataLoaderStreamImpl.class);

    private Connection conn;
    private PreparedStatement dataLoadSt;
    private ResultSet dataLoadRs;

    public void configure(Configuration cfg) throws ConfigurationException {
        try {
            Class.forName(cfg.getStringValue(this, "jdbc-driver-classname"));
            Properties connInfo = new Properties();

            connInfo.put("user", cfg.getStringValue(this, "username"));
            connInfo.put("password", cfg.getStringValue(this, "password"));

            this.conn = DriverManager.getConnection(cfg.getStringValue(this, "dburl"), connInfo);

            dataLoadSt = conn.prepareStatement(getDataLoadSQLQuery(cfg));

            dataLoadRs = dataLoadSt.executeQuery();

        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("JDBC driver class not found", e);
        } catch (SQLException e) {
            throw new ConfigurationException("Can't configure url source", e);
        }

        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws CloseException {
        if (dataLoadRs != null) {
            try {
                dataLoadRs.close();
            } catch (SQLException e) {
                throw new CloseException("Can't close data load result set", e);
            } finally {
                if (dataLoadSt != null) {
                    try {
                        dataLoadSt.close();
                    } catch (SQLException e) {
                        LOG.error("Can't close data load statement", e);
                    } finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                LOG.error("Can't close jdbc connection", e);
                            }
                        }
                    }
                }
            }
        }
    }

    protected String getDataLoadSQLQuery(Configuration cfg) throws ConfigurationException {
        return cfg.getStringValue(this, "data-load-sql-query");
    }

    public abstract T createInstance(ResultSet rs) throws SQLException;

    @Override
    public T next() throws LoadException {
        try {
            return dataLoadRs.next() ? createInstance(dataLoadRs) : null;
        } catch (SQLException e) {
            throw new LoadException("Can't obtain next instance", e);
        }
    }

}
