import java.util.HashMap;
import java.util.Map;

public class CommandLineParser {

    private static final String VFS = "vfs";
    private static final String LOG = "log";
    private static final String SCRIPT = "script";
    private static final String CONFIG = "config";

    private final String[] args;

    public CommandLineParser(String[] args) {
        this.args = args;
    }


    public Map<String, String> parse() {
        Map<String, String> result = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String argument = args[i];

            if (!argument.startsWith("--")) {
                throw new IllegalArgumentException(
                        "Unknown argument: " + argument
                );
            }

            String withoutPrefix = argument.substring(2);

            if (withoutPrefix.contains("=")) {
                parseEqualsArgument(withoutPrefix, result);
            } else {
                i = parseSeparatedArgument(i, withoutPrefix, result);
            }
        }

        return result;
    }

    private void parseEqualsArgument(
            String argument,
            Map<String, String> result
    ) {
        String[] parts = argument.split("=", 2);
        String key = parts[0];
        String value = parts[1];

        validateKey(key);

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing value for --" + key
            );
        }

        putUnique(result, key, value);
    }

    private int parseSeparatedArgument(
            int index,
            String key,
            Map<String, String> result
    ) {
        validateKey(key);

        int valueIndex = index + 1;

        if (valueIndex >= args.length) {
            throw new IllegalArgumentException(
                    "Missing value for --" + key
            );
        }

        String value = args[valueIndex];

        if (value.startsWith("--")) {
            throw new IllegalArgumentException(
                    "Missing value for --" + key
            );
        }

        putUnique(result, key, value);

        return valueIndex;
    }

    private void validateKey(String key) {
        if (!key.equals(VFS)
                && !key.equals(LOG)
                && !key.equals(SCRIPT)
                && !key.equals(CONFIG)) {

            throw new IllegalArgumentException(
                    "Unknown argument: --" + key
            );
        }
    }

    private void putUnique(
            Map<String, String> result,
            String key,
            String value
    ) {
        if (result.containsKey(key)) {
            throw new IllegalArgumentException(
                    "Duplicate argument: --" + key
            );
        }

        result.put(key, value);
    }
}