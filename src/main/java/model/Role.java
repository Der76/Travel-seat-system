package model;

/**
 * All target user roles from Section 2 of the System Proposal.
 * Edit this list if your proposal names different roles - registration,
 * the database CHECK constraint, and login routing all follow it automatically.
 */
public enum Role {
    TEACHER,
    STUDENT,
    STAFF,
    CASHIER,
    ADMIN;   // not selectable at registration - created by the system / existing admin only

    /** Roles a person may pick when registering (ADMIN is excluded). */
    public static Role[] registrable() {
        return java.util.Arrays.stream(values()).filter(r -> r != ADMIN).toArray(Role[]::new);
    }

    public static Role fromString(String value) {
        return Role.valueOf(value.trim().toUpperCase());
    }
}
