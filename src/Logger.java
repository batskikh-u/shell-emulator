import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

public class Logger {

    private static final String HEADER =
            "user,date_time,command,args"
                    + System.lineSeparator();

    private final Path logPath;
    private final String userName;

    public Logger(String logFile) {
        this.logPath = Path.of(logFile);
        this.userName = System.getProperty("user.name");

        createLogFile();
    }

    public void log(String command, List<String> args) {
        String dateTime = LocalDateTime.now().toString();
        String arguments = String.join(" ", args);

        String line = csv(userName) + ","
                + csv(dateTime) + ","
                + csv(command) + ","
                + csv(arguments)
                + System.lineSeparator();

        try {
            Files.writeString(
                    logPath,
                    line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Cannot write to log file: " + logPath,
                    e
            );
        }
    }

    private void createLogFile() {
        try {
            if (logPath.getParent() != null) {
                Files.createDirectories(logPath.getParent());
            }

            if (!Files.exists(logPath)
                    || Files.size(logPath) == 0) {

                Files.writeString(
                        logPath,
                        HEADER,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Cannot create log file: " + logPath,
                    e
            );
        }
    }

    private String csv(String value) {
        String escaped = value.replace("\"", "\"\"");

        return "\"" + escaped + "\"";
    }
}