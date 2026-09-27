import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFileParser {

    private static final String VFS = "vfs";
    private static final String LOG = "log";
    private static final String SCRIPT = "script";

    private final Map<String, String> cliConfigs;
    private final Map<String, String> fileConfigs = new HashMap<>();


    public ConfigFileParser(Map<String, String> cliConfigs) {
        this.cliConfigs = new HashMap<>(cliConfigs);
    }


    public void readConfig(String configPath) {
        Path path = Path.of(configPath);

        try {
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                parseLine(line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Cannot read configuration file: " + configPath,
                    e
            );
        }
    }


    public Configuration createConfiguration(String configPath) {
        String vfsPath = getValue("vfs");
        String logPath = getValue("log");
        String scriptPath = getValue("script");

        if (vfsPath == null
                || logPath == null
                || scriptPath == null) {

            throw new IllegalArgumentException(
                    "Missing required configuration parameter"
            );
        }

        return new Configuration(
                vfsPath,
                logPath,
                scriptPath,
                configPath
        );
    }

    private void parseLine(String line) {
        String trimmed = line.trim();

        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }

        String[] parts = trimmed.split("=", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid configuration line: " + line
            );
        }

        String key = parts[0].trim();
        String value = parts[1].trim();

        validateKey(key);

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Empty value for configuration parameter: " + key
            );
        }

        fileConfigs.put(key, value);
    }

    private String getValue(String key) {
        if (cliConfigs.containsKey(key)) {
            return cliConfigs.get(key);
        }

        return fileConfigs.get(key);
    }

    private void validateKey(String key) {
        if (!key.equals(VFS)
                && !key.equals(LOG)
                && !key.equals(SCRIPT)) {

            throw new IllegalArgumentException(
                    "Unknown configuration parameter: " + key
            );
        }
    }
}