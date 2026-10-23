package ui;

import model.User;

import java.util.Scanner;

public class TeacherMenu extends RoleMenu {

    public TeacherMenu(User user, Scanner scanner) {
        super(user, scanner);
    }

    @Override
    protected String title() {
        return "TEACHER MENU";
    }
}
