package ui;

import model.User;

import java.util.Scanner;

/** Base class for every role-based menu. Subclasses just provide a title and their options. */
public abstract class RoleMenu {

    protected final User user;
    protected final Scanner scanner;

    protected RoleMenu(User user, Scanner scanner) {
        this.user = user;
        this.scanner = scanner;
    }

    protected abstract String title();

    /** Role-specific options, printed before "Logout". Return false if the choice was not handled. */
    protected boolean handleChoice(String choice) {
        return false;
    }

    protected void printOptions() {
        // role-specific options go here in later deliverables
    }

    public void show() {
        System.out.println("\n=== " + title() + " ===");
        System.out.println("Welcome, " + user.username() + "!");
        System.out.println("Role: " + user.roles() + " | Status: " + user.status());

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            printOptions();
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) {
                System.out.println("You have been logged out.");
                loggedIn = false;
            } else if (!handleChoice(choice)) {
                System.out.println("Invalid choice.");
            }
        }
    }
}
