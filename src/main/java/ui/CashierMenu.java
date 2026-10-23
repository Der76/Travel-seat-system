package ui;

import model.User;

import java.util.Scanner;

public class CashierMenu extends RoleMenu {

    public CashierMenu(User user, Scanner scanner) {
        super(user, scanner);
    }

    @Override
    protected String title() {
        return "CASHIER MENU";
    }
}
