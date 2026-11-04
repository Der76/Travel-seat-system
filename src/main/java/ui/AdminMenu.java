package ui;

import dao.UserDao;
import model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private static final List<String> VALID_ROLES = List.of("STUDENT", "TEACHER", "ADMIN");
    private static final List<String> VALID_STATUSES = List.of("APPROVED", "PENDING");

    private final User currentAdmin;
    private final Scanner scanner;
    private final UserDao userDao;

    public AdminMenu(User currentAdmin, Scanner scanner, UserDao userDao) {
        this.currentAdmin = currentAdmin;
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void show() {
        System.out.println("\nWelcome, " + currentAdmin.username() + "!");
        System.out.println("Role: " + currentAdmin.roles() + " | Status: " + currentAdmin.status());

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. View All Users");
            System.out.println("2. Update User Role and Status");
            System.out.println("3. Logout");

            int choice = readIntInput("Choose: ");
            switch (choice) {
                case 1 -> displayUsersTable();
                case 2 -> updateUser();
                case 3 -> {
                    System.out.println("You have been logged out.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------- Update feature ----------------

    private void updateUser() {
        System.out.println("\n--- Update User Role and Status ---");
        displayUsersTable();

        // Step 1: target user ID
        User target = null;
        while (target == null) {
            int targetUserId = readIntInput("Enter User ID to update (0 to cancel): ");
            if (targetUserId == 0) {
                System.out.println("Update cancelled.");
                return;
            }
            if (targetUserId == currentAdmin.id()) {
                System.out.println("WARNING: Modifying the currently active admin account is restricted.");
                continue;
            }
            target = findUserById(targetUserId);
            if (target == null) {
                System.out.println("No user found with ID " + targetUserId + ". Try again.");
            }
        }

        // Step 2: new role
        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // Step 3: new status
        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(target.id(), newRole, newStatus)) {
            System.out.println("SUCCESS: User '" + target.username() + "' (ID " + target.id()
                    + ") updated -> Role: " + newRole + " | Status: " + newStatus);
        } else {
            System.out.println("Update failed. No changes were made.");
        }
    }

    private User findUserById(int id) {
        for (User u : userDao.getAllUsers()) {
            if (u.id() == id) {
                return u;
            }
        }
        return null;
    }

    private void displayUsersTable() {
        List<User> users = userDao.getAllUsers();
        String line = "+-------+----------------------+------------+------------+";
        System.out.println();
        System.out.println(line);
        System.out.printf("| %-5s | %-20s | %-10s | %-10s |%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(line);
        if (users.isEmpty()) {
            System.out.printf("| %-53s |%n", "No users found.");
        }
        for (User u : users) {
            System.out.printf("| %-5d | %-20s | %-10s | %-10s |%n",
                    u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(line);
    }

    // ---------------- Input validation wrappers ----------------

    // Keeps asking until the person types a whole number (no crash on letters/symbols)
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Returns STUDENT / TEACHER / ADMIN, or null if the user typed 0 to cancel
    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (STUDENT, TEACHER, ADMIN) or 0 to cancel: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_ROLES.contains(input)) {
                return input;
            }
            System.out.println("Invalid role. Please enter STUDENT, TEACHER, or ADMIN.");
        }
    }

    // Returns APPROVED / PENDING, or null if the user typed 0 to cancel
    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING) or 0 to cancel: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_STATUSES.contains(input)) {
                return input;
            }
            System.out.println("Invalid status. Please enter APPROVED or PENDING.");
        }
    }
}
