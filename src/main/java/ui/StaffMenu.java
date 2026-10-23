package ui;

import model.User;

import java.util.Scanner;

public class StaffMenu extends RoleMenu {

    public StaffMenu(User user, Scanner scanner) {
        super(user, scanner);
    }

    @Override
    protected String title() {
        return "STAFF MENU";
    }
}
