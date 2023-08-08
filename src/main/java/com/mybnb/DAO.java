package com.mybnb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;

import io.github.cdimascio.dotenv.Dotenv;

public final class DAO {

    private static final String CONNECTION_ENDPOINT = "jdbc:mysql://127.0.0.1/" + Dotenv.load().get("DB_NAME");
    private static final String DB_USER = Dotenv.load().get("DB_USER");
    private static final String DB_PASSWORD = Dotenv.load().get("DB_PASSWORD");

    private static DAO INSTANCE;
    private Connection connection;

    private DAO() throws SQLException {
        System.out.println("Connecting to database...");
        this.connection = DriverManager.getConnection(
                CONNECTION_ENDPOINT,
                DB_USER,
                DB_PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public static DAO getInstance() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new DAO();
        }

        return INSTANCE;
    }

    public static interface GetHandler {
        public JSONObject execute(ResultSet rs) throws SQLException;
    }

    public JSONObject get(String sqlQuery, GetHandler fn) throws BaseSQLStatusException {
        try (Statement stmt = this.connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sqlQuery);
            System.out.println(String.format("QUERY: %s", sqlQuery));

            JSONObject response = fn.execute(rs);

            rs.close();
            stmt.close();

            return response;
        } catch (SQLException e) {
            throw SQLStatusExceptions.getException(e);
        }
    }

    public int create(Map<String, Object> mapping, String tableName, boolean returnsId) throws BaseSQLStatusException {
        String sqlQuery = this.buildInsertionQuery(mapping, tableName);
        return this.runUpdateQuery(sqlQuery, returnsId);
    }

    public int update(Map<String, Object> newMapping, Map<String, Object> keyConditions,
            String tableName, boolean returnsId) throws BaseSQLStatusException {
        String sqlQuery = this.buildUpdateQuery(newMapping, keyConditions, tableName);
        return this.runUpdateQuery(sqlQuery, returnsId);
    }

    public int delete(Map<String, Object> keyConditions, String tableName) throws BaseSQLStatusException {
        String sqlQuery = this.buildDeleteQuery(keyConditions, tableName);
        return this.runUpdateQuery(sqlQuery, false);
    }

    private int runUpdateQuery(String sqlQuery, boolean returnsId) throws BaseSQLStatusException {
        try {
            Statement stmt = this.connection.createStatement();
            int res = stmt.executeUpdate(sqlQuery);

            if (returnsId) {
                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID() AS lastId");

                while (rs.next()) {
                    res = rs.getInt("lastId");
                }
            }

            stmt.close();

            System.out.println(String.format("QUERY: %s", sqlQuery));
            return res;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw SQLStatusExceptions.getException(e);
        }
    }

    private String buildInsertionQuery(Map<String, Object> mapping, String tableName) {

        ArrayList<String> columnNames = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();

        for (Map.Entry<String, Object> m : mapping.entrySet()) {
            columnNames.add(m.getKey());

            if (m.getValue().getClass().getSimpleName().equals("Integer")) {
                values.add(String.format("%s", m.getValue()));
            } else {
                values.add(String.format("'%s'", m.getValue()));
            }
        }

        String c = columnNames.toString().replace("[", "(").replace("]", ")");
        String v = values.toString().replace("[", "(").replace("]", ")");

        return String.format("INSERT INTO %s %s VALUES %s", tableName, c, v);
    }

    private String buildUpdateQuery(Map<String, Object> newMapping, Map<String, Object> keyConditions,
            String tableName) {
        ArrayList<String> updates = new ArrayList<String>();
        ArrayList<String> keys = new ArrayList<String>();

        for (Map.Entry<String, Object> m : newMapping.entrySet()) {

            String v = m.getKey() + "=";

            if (m.getValue().getClass().getSimpleName().equals("Integer")) {
                v += String.format("%s", m.getValue());
            } else {
                v += String.format("'%s'", m.getValue());
            }

            updates.add(v);
        }

        String updatesAsString = updates.toString().replace("[", "").replace("]", "");

        for (Map.Entry<String, Object> m : keyConditions.entrySet()) {

            String v = m.getKey() + "=";

            if (m.getValue().getClass().getSimpleName().equals("Integer")) {
                v += String.format("%s", m.getValue());
            } else {
                v += String.format("'%s'", m.getValue());
            }

            keys.add(v);
        }

        String keysAsString = keys.toString().replace("[", "").replace("]", "").replace(", ", " AND ");

        return String.format("UPDATE %s SET %s WHERE %s", tableName, updatesAsString, keysAsString);
    }

    private String buildDeleteQuery(Map<String, Object> keyConditions, String tableName) {
        ArrayList<String> keys = new ArrayList<String>();

        for (Map.Entry<String, Object> m : keyConditions.entrySet()) {

            String v = m.getKey() + "=";

            if (m.getValue().getClass().getSimpleName().equals("Integer")) {
                v += String.format("%s", m.getValue());
            } else {
                v += String.format("'%s'", m.getValue());
            }

            keys.add(v);
        }

        String keysAsString = keys.toString().replace("[", "").replace("]", "").replace(", ", " AND ");

        return String.format("DELETE FROM %s WHERE %s", tableName, keysAsString);
    }
}
