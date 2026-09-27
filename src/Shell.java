import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Shell {

    private static final int COMMAND_INDEX = 0;
    private static final int ARGUMENTS_START = 1;
    private static final String EXIT_COMMAND = "exit";

    private final String vfsName;
    private final CommandParser parser;
    private final CommandHandler handler;
    private final Logger logger;
    private final String scriptPath;

    public Shell(String vfsPath, Logger logger, String scriptPath) {
        this.vfsName = createVfsName(vfsPath);
        this.logger = logger;
        this.scriptPath = scriptPath;
        this.parser = new CommandParser();
        this.handler = new CommandHandler();
    }

    public void process() {
        runStartupScript();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(vfsName + "> ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                if (executeLine(scanner.nextLine())) {
                    break;
                }
            }
        }
    }

    private void runStartupScript() {
        try {
            List<String> lines = Files.readAllLines(
                    Path.of(scriptPath),
                    StandardCharsets.UTF_8
            );

            for (String line : lines) {
                processScriptLine(line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Cannot read startup script: " + scriptPath,
                    e
            );
        }
    }

    private void processScriptLine(String line) {
        String commandLine = line.trim();

        if (isComment(commandLine)) {
            return;
        }

        System.out.println(vfsName + "> " + commandLine);

        if (executeLine(commandLine)) {
            throw new IllegalArgumentException(
                    "Startup script requested exit"
            );
        }
    }

    private boolean executeLine(String line) {
        try {
            List<String> tokens = parser.parse(line);

            if (tokens.isEmpty()) {
                return false;
            }

            String command = tokens.get(COMMAND_INDEX);
            List<String> args = tokens.subList(
                    ARGUMENTS_START,
                    tokens.size()
            );

            logger.log(command, args);

            if (EXIT_COMMAND.equals(command)) {
                System.out.println("The session is over");
                return true;
            }

            handler.execute(command, args);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return false;
    }

    private boolean isComment(String line) {
        return line.isEmpty() || line.startsWith("#");
    }

    private String createVfsName(String vfsPath) {
        Path fileName = Path.of(vfsPath).getFileName();
        return fileName == null ? vfsPath : fileName.toString();
    }
}