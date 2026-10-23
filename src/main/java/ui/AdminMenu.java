package ui;

import dao.UserDao;
import model.User;

import java.util.List;
import java.util.Scanner;

/** Admin menu: view all users, view pending registrations, and approve accounts. */
public class AdminMenu extends RoleMenu {

    private final UserDao userDao;

    public AdminMenu(User user, Scanner scanner, UserDao userDao) {
        super(user, scanner);
        this.userDao = userDao;
    }

    @Override
    protected String title() {
        return "ADMIN MENU";
    }

    @Override
    protected void printOptions() {
        System.out.println("1. View all system users");
        System.out.println("2. View pending registrations");
        System.out.println("3. Approve user account");
    }

    @Override
    protected boolean handleChoice(String choice) {
        switch (choice) {
            case "1" -> viewAllUsers();
            case "2" -> viewPendingUsers();
            case "3" -> approveUser();
            default -> { return false; }
        }
        return true;
    }

    private void viewAllUsers() {
        System.out.println("\n--- All System Users ---");
        printUserTable(userDao.getAllUsers());
    }

    private void viewPendingUsers() {
        System.out.println("\n--- Pending Registrations ---");
        printUserTable(userDao.getPendingUsers());
    }

    private void approveUser() {
        List<User> pending = userDao.getPendingUsers();
        System.out.println("\n--- Approve User Account ---");
        printUserTable(pending);
        if (pending.isEmpty()) {
            return;
        }

        while (true) {
            System.out.print("Enter Target User ID to approve (0 to cancel): ");
            String input = scanner.nextLine().trim();

            int id;
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (id == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            boolean inPendingList = pending.stream().anyMatch(u -> u.id() == id);
            if (!inPendingList) {
                System.out.println("ID " + id + " is not in the pending list. Try again.");
                continue;
            }

            if (userDao.approveUser(id)) {
                System.out.println("User #" + id + " has been APPROVED.");
            } else {
                System.out.println("Approval failed.");
            }
            return;
        }
    }

    /** Prints any list of users as an aligned, bordered table. */
    private void printUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users to display.");
            return;
        }
        String border = "+" + "-".repeat(7) + "+" + "-".repeat(17) + "+" + "-".repeat(14) + "+" + "-".repeat(12) + "+";
        String row = "| %-5s | %-15s | %-12s | %-10s |%n";

        System.out.println(border);
        System.out.printf(row, "ID", "Username", "Role", "Status");
        System.out.println(border);
        for (User u : users) {
            System.out.printf(row, u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(border);
        System.out.println(users.size() + " user(s).");
    }
}
