package nl.riebie.mcclans.database;

import nl.riebie.mcclans.config.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnectionOwner {

    private static DatabaseConnectionOwner instance;

    private DatabaseConnection databaseConnection = new DatabaseConnection();

    protected DatabaseConnectionOwner() {
    }

    public static DatabaseConnectionOwner getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionOwner();
        }
        return instance;
    }

    public boolean setupConnection() {
        return databaseConnection.setupConnection(
                Config.getString(Config.DATABASE_SERVER),
                Config.getInteger(Config.DATABASE_SERVER_PORT),
                Config.getString(Config.DATABASE_NAME),
                Config.getString(Config.DATABASE_SERVER_USER),
                Config.getString(Config.DATABASE_SERVER_PASSWORD)
        );
    }

    public void executeStatement(String statement) {
        databaseConnection.executeStatement(statement);
    }

    public void executeTransactionStatement(PreparedStatement statement) throws SQLException {
        databaseConnection.executeTransactionStatement(statement);
    }

    public ResultSet executeQuery(String query) {
        return databaseConnection.executeQuery(query);
    }

    public void startTransaction() throws SQLException {
        databaseConnection.startTransaction();
    }

    public void commitTransaction() throws SQLException {
        databaseConnection.commitTransaction();
    }

    public void cancelTransaction() {
        databaseConnection.cancelTransaction();
    }

    public boolean isValid() {
        return databaseConnection.isValid();
    }

    public void close() {
        databaseConnection.close();
    }

    public Connection getConnection() {
        return databaseConnection.connection();
    }
}
