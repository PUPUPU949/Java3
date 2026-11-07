import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private String fileName;

    public Logger(String fileName) {
        this.fileName = fileName;
    }

    public void log(String message) throws IOException {

        String time = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        );

        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(fileName),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write("[" + time + "] " + message);
            writer.newLine();
        }
    }
}