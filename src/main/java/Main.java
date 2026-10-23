import dao.DatabaseInitializer;
import dao.UserDao;
import ui.Authentication;

import java.util.Scanner;

/** Single startup entry point: initialize DB -> create shared objects -> hand off to Authentication. */
public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        new Authentication(userDao, scanner).start();

        scanner.close();
    }
}
