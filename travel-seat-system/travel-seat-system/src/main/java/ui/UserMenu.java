package ui;

import model.User;

import java.util.Scanner;

public class UserMenu {

    private final User user;
    private final Scanner scanner;

    public UserMenu(User user, Scanner scanner) {
        this.user = user;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\nWelcome, " + user.username() + "!");
        System.out.println("Role: " + user.roles() + " | Status: " + user.status());

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n1. Logout");
            System.out.print("Choose: ");
            if (scanner.nextLine().trim().equals("1")) {
                System.out.println("You have been logged out.");
                loggedIn = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
