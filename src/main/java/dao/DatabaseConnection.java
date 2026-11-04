package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:travel.db";

    private static final String CREATE_USERS_SQL = """
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles    TEXT NOT NULL CHECK (roles IN ('ADMIN', 'TEACHER', 'STUDENT')),
                status   TEXT NOT NULL DEFAULT 'PENDING'
            )
            """;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Creates/upgrades the users table and makes sure at least one ADMIN exists
    public static void initialize() {
        try (Connection conn = getConnection()) {
            migrateOldTableIfNeeded(conn);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(CREATE_USERS_SQL);
            }
            seedDefaultAdmin(conn);
        } catch (SQLException e) {
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }

    // Old databases only allowed TEACHER/STUDENT in the CHECK constraint.
    // SQLite cannot alter a CHECK, so we rebuild the table and copy the rows over.
    private static void migrateOldTableIfNeeded(Connection conn) throws SQLException {
        String tableSql = null;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT sql FROM sqlite_master WHERE type = 'table' AND name = 'users'")) {
            if (rs.next()) {
                tableSql = rs.getString(1);
            }
        }
        if (tableSql == null || tableSql.contains("'ADMIN'")) {
            return; // no table yet, or already up to date
        }

        conn.setAutoCommit(false);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users RENAME TO users_old");
            stmt.execute(CREATE_USERS_SQL);
            stmt.execute("INSERT INTO users (id, username, password, roles, status) "
                    + "SELECT id, username, password, roles, status FROM users_old");
            stmt.execute("DROP TABLE users_old");
            conn.commit();
            System.out.println("Database upgraded: ADMIN role is now supported.");
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Creates a default admin (admin / admin123) only if no ADMIN exists yet
    private static void seedDefaultAdmin(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 FROM users WHERE roles = 'ADMIN' LIMIT 1")) {
            if (rs.next()) {
                return;
            }
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT OR IGNORE INTO users (username, password, roles, status) "
                    + "VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')");
            System.out.println("Default admin created -> username: admin | password: admin123");
        }
    }
}
