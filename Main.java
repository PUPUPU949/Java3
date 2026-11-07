import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws Exception {

        Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("=== Задание №1 ===");

        Logger logger = new Logger("log.txt");

        logger.log("Программа запущена");
        logger.log("Выполнение задания Logger завершено");

        System.out.println("Сообщения успешно записаны в файл log.txt");

        System.out.println("\n=== Задание №2 ===");

        UserManager manager = new UserManager("users.txt");

        System.out.print("Введите логин нового пользователя: ");
        String login = in.nextLine();

        System.out.print("Введите пароль: ");
        String password = in.nextLine();

        if (manager.addUser(login, password)) {
            System.out.println("Пользователь успешно добавлен!");
        } else {
            System.out.println("Не удалось добавить пользователя!");
        }

        System.out.print("\nВведите логин для проверки: ");
        String checkLogin = in.nextLine();

        System.out.print("Введите пароль для проверки: ");
        String checkPassword = in.nextLine();

        if (manager.authenticate(checkLogin, checkPassword)) {
            System.out.println("Авторизация успешна!");
        } else {
            System.out.println("Неверный логин или пароль!");
        }
    }
}