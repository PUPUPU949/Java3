import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserManager {

    private String fileName;

    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;

    public UserManager(String fileName) {
        this.fileName = fileName;
    }

    // Создание хеша пароля
    private String hashPassword(String password, byte[] salt)
            throws Exception {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
            );

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);

        } finally {
            spec.clearPassword();
        }
    }

    // Добавление пользователя
    public boolean addUser(String login, String password)
            throws Exception {

        if (login == null || login.isEmpty() ||
                login.contains("|") || password == null ||
                password.isEmpty()) {
            return false;
        }

        if (userExists(login)) {
            return false;
        }

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        String saltText = Base64.getEncoder()
                .encodeToString(salt);

        String passwordHash = hashPassword(password, salt);

        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(fileName),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(login + "|" + saltText + "|" + passwordHash);
            writer.newLine();
        }

        return true;
    }

    // Проверка существования пользователя
    private boolean userExists(String login) throws IOException {

        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            return false;
        }

        List<String> lines = Files.readAllLines(
                path, StandardCharsets.UTF_8
        );

        for (String line : lines) {
            String[] data = line.split("\\|");

            if (data.length == 3 && data[0].equals(login)) {
                return true;
            }
        }

        return false;
    }

    // Проверка логина и пароля
    public boolean authenticate(String login, String password)
            throws Exception {

        Path path = Path.of(fileName);

        if (!Files.exists(path) || login == null || password == null) {
            return false;
        }

        List<String> lines = Files.readAllLines(
                path, StandardCharsets.UTF_8
        );

        for (String line : lines) {

            String[] data = line.split("\\|");

            if (data.length == 3 && data[0].equals(login)) {

                byte[] salt = Base64.getDecoder()
                        .decode(data[1]);

                String enteredHash = hashPassword(password, salt);

                byte[] storedHash = Base64.getDecoder()
                        .decode(data[2]);

                byte[] newHash = Base64.getDecoder()
                        .decode(enteredHash);

                return MessageDigest.isEqual(storedHash, newHash);
            }
        }

        return false;
    }
}