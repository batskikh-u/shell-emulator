import java.util.Map;

public class Main {

    private static final String DEFAULT_CONFIG =
            "config/config.ini";

    public static void main(String[] args) {
        try {
            Map<String, String> cliConfigs =
                    parseCommandLine(args);

            String configPath =
                    getConfigPath(cliConfigs);

            ConfigFileParser configParser =
                    new ConfigFileParser(cliConfigs);

            configParser.readConfig(configPath);

            Configuration configuration =
                    configParser.createConfiguration(
                            configPath
                    );

            printConfiguration(configuration);

            Logger logger =
                    new Logger(configuration.getLogPath());

            Shell shell =
                    new Shell(
                            configuration.getVfsPath(),
                            logger,
                            configuration.getScriptPath()
                    );

            shell.process();
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }

    private static Map<String, String> parseCommandLine(
            String[] args
    ) {
        CommandLineParser parser =
                new CommandLineParser(args);

        return parser.parse();
    }

    private static String getConfigPath(
            Map<String, String> cliConfigs
    ) {
        String configPath = cliConfigs.get("config");

        if (configPath == null) {
            return DEFAULT_CONFIG;
        }

        return configPath;
    }

    private static void printConfiguration(
            Configuration configuration
    ) {
        System.out.println(
                "VFS: " + configuration.getVfsPath()
        );

        System.out.println(
                "Log: " + configuration.getLogPath()
        );

        System.out.println(
                "Script: " + configuration.getScriptPath()
        );

        System.out.println(
                "Config: " + configuration.getConfigPath()
        );
    }
}