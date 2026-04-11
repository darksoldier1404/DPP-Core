package com.darksoldier1404.dppc.data.sql;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

/**
 * Use the MySQL constructor for remote databases, or the SQLite constructor for
 * file-based embedded databases.
 *
 * <pre>{@code
 * // MySQL
 * DBConfig mysql = new DBConfig("localhost", 3306, "mydb", "root", "pass", "prefix_");
 *
 * // SQLite (path is relative to plugin data folder)
 * DBConfig sqlite = new DBConfig("database.db", "prefix_");
 * }</pre>
 */
@DPPCoreVersion(since = "5.4.0")
public class DBConfig {

    private final DBType dbType;

    /* MySQL fields */
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    /* SQLite field */
    private final String filePath;

    /* Common */
    private final String tablePrefix;

    /**
     * Creates a MySQL / MariaDB configuration.
     *
     * @param host        Database host (e.g. {@code "localhost"})
     * @param port        Database port (e.g. {@code 3306})
     * @param database    Schema / database name
     * @param username    Login username
     * @param password    Login password
     * @param tablePrefix Prefix applied to every table name (may be empty)
     */
    public DBConfig(String host, int port, String database,
                    String username, String password, String tablePrefix) {
        this.dbType = DBType.MYSQL;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.filePath = null;
        this.tablePrefix = tablePrefix != null ? tablePrefix : "";
    }

    /**
     * Creates a SQLite configuration.
     *
     * @param filePath    SQLite file path relative to the plugin data folder
     *                    (e.g. {@code "database.db"} or {@code "db/data.db"})
     * @param tablePrefix Prefix applied to every table name (may be empty)
     */
    public DBConfig(String filePath, String tablePrefix) {
        this.dbType = DBType.SQLITE;
        this.host = "localhost";
        this.port = 3306;
        this.database = "dpplugins";
        this.username = "root";
        this.password = "";
        this.filePath = filePath;
        this.tablePrefix = tablePrefix != null ? tablePrefix : "";
    }

    public DBType getDbType() {
        return dbType;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * SQLite file path, relative to the plugin data folder.
     */
    public String getFilePath() {
        return filePath;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }
}

