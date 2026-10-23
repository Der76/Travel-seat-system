package ui;

import model.User;

import java.util.Scanner;

public class StudentMenu extends RoleMenu {

    public StudentMenu(User user, Scanner scanner) {
        super(user, scanner);
    }

    @Override
    protected String title() {
        return "STUDENT MENU";
    }
}
