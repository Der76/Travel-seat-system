package ui;

import dao.UserDao;
import model.Role;
import model.User;

import java.util.Optional;
import java.util.Scanner;

/** Handles registration, login, and routing each user to the menu for their role. */
public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    // Single shared UserDao and Scanner are injected - nothing is created here.
    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== TRAVEL SEAT AND RESERVATION SYSTEM ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken.");
            return;
        }

        Role role = chooseRole();

        if (userDao.register(username, password, role.name())) {
            System.out.println("Registered as " + role + ". Status: PENDING.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private Role chooseRole() {
        Role[] roles = Role.registrable();
        while (true) {
            System.out.println("Select role:");
            for (int i = 0; i < roles.length; i++) {
                System.out.println("  " + (i + 1) + ". " + roles[i]);
            }
            System.out.print("Role: ");
            String input = scanner.nextLine().trim();
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < roles.length) {
                    return roles[index];
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            System.out.println("Please enter a number from 1 to " + roles.length + ".");
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Optional<User> result = userDao.login(username, password);
        if (result.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }
        User user = result.get();
        switch (user.status()) {
            case "APPROVED" -> routeToMenu(user);   // returns here after logout
            case "PENDING"  -> System.out.println("Your account is still PENDING. Please wait for an administrator to approve it.");
            case "REJECTED" -> System.out.println("Your registration was REJECTED. Please contact an administrator.");
            default         -> System.out.println("Account status '" + user.status() + "' cannot log in.");
        }
    }

    private void routeToMenu(User user) {
        Role role;
        try {
            role = Role.fromString(user.roles());
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown role '" + user.roles() + "'. Contact an administrator.");
            return;
        }

        RoleMenu menu = switch (role) {
            case TEACHER -> new TeacherMenu(user, scanner);
            case STUDENT -> new StudentMenu(user, scanner);
            case STAFF   -> new StaffMenu(user, scanner);
            case CASHIER -> new CashierMenu(user, scanner);
            case ADMIN   -> new AdminMenu(user, scanner, userDao);
        };
        menu.show();
    }
}
