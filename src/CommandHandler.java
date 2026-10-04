import java.util.List;

public class CommandHandler {

    private static final int DEFAULT_TAIL_LINES = 10;
    private static final int MIN_ARGUMENTS = 1;
    private static final int TAIL_OPTION_ARGUMENTS = 3;
    private static final int NO_LINES = 0;
    private static final long MILLISECONDS_IN_SECOND = 1000;
    private static final long SECONDS_IN_MINUTE = 60;
    private final long startTime;
    private final VirtualFileSystem vfs;
    private final Logger logger;
    private String currentDirectory = "";

    public CommandHandler(VirtualFileSystem vfs, Logger logger) {
        this.vfs = vfs;
        this.logger = logger;
        this.startTime = System.currentTimeMillis();
    }

    public void execute(String command, List<String> args) {

        if (command.equals("ls")) {
            executeLs(args);
            return;
        }

        if (command.equals("cd")) {
            executeCd(args);
            return;
        }

        if (command.equals("tail")) {
            executeTail(args);
            return;
        }

        if (command.equals("history")) {
            executeHistory(args);
            return;
        }
        if (command.equals("uptime")) {
            executeUptime(args);
            return;
        }

        throw new IllegalArgumentException(
                "unknown command " + command
        );
    }

    private void executeLs(List<String> args) {

        if (args.size() > 1) {
            throw new IllegalArgumentException(
                    "ls accepts at most one argument"
            );
        }

        String directory = currentDirectory;

        if (!args.isEmpty()) {
            directory = resolvePath(args.get(0));
        }

        if (!vfs.isDirectory(directory)) {
            throw new IllegalArgumentException(
                    "not a directory: " + args.get(0)
            );
        }

        List<String> files = vfs.list(directory);

        for (String file : files) {
            System.out.println(file);
        }
    }

    private void executeCd(List<String> args) {

        if (args.size() != 1) {
            throw new IllegalArgumentException(
                    "cd requires exactly one argument"
            );
        }

        String newDirectory = resolvePath(args.get(0));

        if (!vfs.exists(newDirectory)) {
            throw new IllegalArgumentException(
                    "directory not found: " + args.get(0)
            );
        }

        if (!vfs.isDirectory(newDirectory)) {
            throw new IllegalArgumentException(
                    "not a directory: " + args.get(0)
            );
        }

        currentDirectory = newDirectory;
    }

    private String resolvePath(String path) {
        path = path.replace('\\', '/');

        if (path.equals(".")) {
            return currentDirectory;
        }

        if (path.equals("..")) {
            return getParent(currentDirectory);
        }

        if (path.startsWith("/")) {
            return normalize(path);
        }

        if (currentDirectory.isEmpty()) {
            return normalize(path);
        }

        return normalize(currentDirectory + "/" + path);
    }

    private String getParent(String path) {
        if (path.isEmpty()) {
            return "";
        }

        int slash = path.lastIndexOf('/');

        if (slash == -1) {
            return "";
        }

        return path.substring(0, slash);
    }

    private String normalize(String path) {
        String[] parts = path.split("/");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {

            if (part.isEmpty() || part.equals(".")) {
                continue;
            }

            if (part.equals("..")) {

                if (result.length() > 0) {
                    int slash = result.lastIndexOf("/");

                    if (slash == -1) {
                        result.setLength(0);
                    } else {
                        result.delete(slash, result.length());
                    }
                }

                continue;
            }

            if (result.length() > 0) {
                result.append("/");
            }

            result.append(part);
        }

        return result.toString();
    }

    private void executeTail(List<String> args) {

        validateTailArguments(args);

        int count = DEFAULT_TAIL_LINES;
        String filePath = args.get(0);

        if (args.get(0).equals("-n")) {
            count = parseTailCount(args);
            filePath = args.get(2);
        }

        String path = resolvePath(filePath);

        validateTailFile(path, filePath);

        printLastLines(path, count);
    }

    private void validateTailArguments(List<String> args) {

        if (args.isEmpty()) {
            throw new IllegalArgumentException(
                    "tail requires a file"
            );
        }

        if (args.get(0).equals("-n")
                && args.size() < TAIL_OPTION_ARGUMENTS) {
            throw new IllegalArgumentException(
                    "tail -n requires a number and a file"
            );
        }

        if (!args.get(0).equals("-n")
                && args.size() > MIN_ARGUMENTS) {
            throw new IllegalArgumentException(
                    "tail accepts one file"
            );
        }

        if (args.get(0).equals("-n")
                && args.size() > TAIL_OPTION_ARGUMENTS) {
            throw new IllegalArgumentException(
                    "tail accepts one file"
            );
        }
    }

    private int parseTailCount(List<String> args) {

        int count;

        try {
            count = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "invalid line count: " + args.get(1)
            );
        }

        if (count <= NO_LINES) {
            throw new IllegalArgumentException(
                    "line count must be positive"
            );
        }

        return count;
    }

    private void validateTailFile(
            String path,
            String filePath
    ) {

        if (!vfs.exists(path)) {
            throw new IllegalArgumentException(
                    "file not found: " + filePath
            );
        }

        if (!vfs.isFile(path)) {
            throw new IllegalArgumentException(
                    "not a file: " + filePath
            );
        }
    }

    private void printLastLines(String path, int count) {

        String content = vfs.readFile(path);
        String[] lines = content.split("\\R");

        int start = Math.max(
                NO_LINES,
                lines.length - count
        );

        for (int i = start; i < lines.length; i++) {
            System.out.println(lines[i]);
        }
    }

    private void executeHistory(List<String> args) {

        if (args.size() > 1) {
            throw new IllegalArgumentException("history accepts at most one argument");
        }

        List<String> history = logger.getHistory();

        int count = history.size();

        if (!args.isEmpty()) {
            try {
                count = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid history count: " + args.get(0));
            }

            if (count <= 0) {
                throw new IllegalArgumentException("history count must be positive");
            }
        }

        int start = Math.max(0, history.size() - count);

        for (int i = start; i < history.size(); i++) {
            System.out.println(
                    (i + 1) + "  " + history.get(i)
            );
        }
    }

    private void executeUptime(List<String> args) {

        if (!args.isEmpty()) {
            throw new IllegalArgumentException("uptime does not accept arguments");
        }

        long elapsedMilliseconds = System.currentTimeMillis() - startTime;

        long totalSeconds = elapsedMilliseconds / MILLISECONDS_IN_SECOND;

        long minutes = totalSeconds / SECONDS_IN_MINUTE;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;

        System.out.println("Uptime: " + minutes + " minutes " + seconds + " seconds");
    }
}