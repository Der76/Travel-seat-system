package dao;

import model.Role;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

/** Makes sure every required table exists before the CLI menu starts. */
public class DatabaseInitializer {

    public static void initialize() {
        String allowedRoles = Arrays.stream(Role.values())
                .map(r -> "'" + r.name() + "'")
                .collect(Collectors.joining(", "));

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles    TEXT NOT NULL CHECK (roles IN (%s)),
                    status   TEXT NOT NULL DEFAULT 'PENDING'
                )
                """.formatted(allowedRoles);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            migrateOldUsersTable(conn, stmt, usersTable);
            stmt.execute(usersTable);
            seedDefaultAdmin(stmt);
            // Add future tables here (e.g. trips, seats, reservations) so they exist before the menu starts.
            System.out.println("Database initialized (travel.db).");
        } catch (SQLException e) {
            System.out.println("Database setup failed: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Older databases were created before the ADMIN role existed, so their CHECK constraint
     * rejects 'ADMIN'. If that is the case, rebuild the table once and keep all existing users.
     */
    private static void migrateOldUsersTable(Connection conn, Statement stmt, String usersTable) throws SQLException {
        String existingSql = null;
        try (ResultSet rs = stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type='table' AND name='users'")) {
            if (rs.next()) existingSql = rs.getString(1);
        }
        if (existingSql == null || existingSql.contains("'ADMIN'")) return;   // fresh DB or already migrated

        stmt.execute("ALTER TABLE users RENAME TO users_old");
        stmt.execute(usersTable);
        stmt.execute("INSERT INTO users (id, username, password, roles, status) "
                + "SELECT id, username, password, roles, status FROM users_old");
        stmt.execute("DROP TABLE users_old");
        System.out.println("Users table upgraded to support the ADMIN role.");
    }

    /** Creates the first admin so someone can approve pending accounts. Does nothing if it already exists. */
    private static void seedDefaultAdmin(Statement stmt) throws SQLException {
        int created = stmt.executeUpdate(
                "INSERT OR IGNORE INTO users (username, password, roles, status) "
                + "VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')");
        if (created > 0) {
            System.out.println("Default admin created -> username: admin | password: admin123 (change this later!)");
        }
    }
}
