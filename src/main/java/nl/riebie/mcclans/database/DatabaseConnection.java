package nl.riebie.mcclans.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.enums.DBMSType;

public class DatabaseConnection {
    private static final String MYSQL_DRIVER_NAME = "mysql";
    private static final String SQLITE_DRIVER_NAME = "sqlite";
    private static final String MYSQL_DRIVER_CLASSPATH = "com.mysql.jdbc.Driver";
    private static final String SQLITE_DRIVER_CLASSPATH = "org.sqlite.JDBC";

    private Connection con = null;

    public boolean setupConnection(String server, int port, String database, String user, String password) {
        String driverName;
        String driverClasspath;

        DBMSType dbmsType = DBMSType.getType(Config.getString(Config.DBMS_TYPE));
        if (dbmsType.equals(DBMSType.MYSQL)) {
            driverName = MYSQL_DRIVER_NAME;
            driverClasspath = MYSQL_DRIVER_CLASSPATH;
        } else if (dbmsType.equals(DBMSType.SQLITE)) {
            driverName = SQLITE_DRIVER_NAME;
            driverClasspath = SQLITE_DRIVER_CLASSPATH;
        } else {
            return false;
        }

        String url = "jdbc:" + driverName + "://" + server + ":" + String.valueOf(port) + "/" + database;
        try {
            Class.forName(driverClasspath);
            con = DriverManager.getConnection(url, user, password);
            return true;

        } catch (Exception e) {
            if (Config.getBoolean(Config.DEBUGGING)) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void executeTransactionStatement(PreparedStatement statement) throws SQLException {
        statement.execute();
    }

    public void executeStatement(String statement) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement(statement);
            preparedStatement.execute();
        } catch (SQLException e) {
            if (Config.getBoolean(Config.DEBUGGING)) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            if (Config.getBoolean(Config.DEBUGGING)) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void startTransaction() throws SQLException {
        con.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        con.commit();
        con.setAutoCommit(true);
    }

    public void cancelTransaction() {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            if (Config.getBoolean(Config.DEBUGGING)) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            if (Config.getBoolean(Config.DEBUGGING)) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid() {
        try {
            return con.isValid(5);
        } catch (SQLException e) {
            if (Config.getBoolean(Config.DEBUGGING)) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Connection connection() {
        return con;
    }
}
