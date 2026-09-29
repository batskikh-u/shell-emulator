import java.util.List;

public class CommandHandler {

    private final VirtualFileSystem vfs;
    private String currentDirectory = "";

    public CommandHandler(VirtualFileSystem vfs) {
        this.vfs = vfs;
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

        throw new IllegalArgumentException(
                "unknown command " + command
        );
    }

    private void executeLs(List<String> args) {
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
        if (args.isEmpty()) {
            throw new IllegalArgumentException(
                    "cd requires an argument"
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
}