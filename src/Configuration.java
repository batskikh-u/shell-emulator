public class Configuration {

    private final String vfsPath;
    private final String logPath;
    private final String scriptPath;
    private final String configPath;

    public Configuration(
            String vfsPath,
            String logPath,
            String scriptPath,
            String configPath
    ) {
        this.vfsPath = vfsPath;
        this.logPath = logPath;
        this.scriptPath = scriptPath;
        this.configPath = configPath;
    }

    public String getVfsPath() {
        return vfsPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public String getConfigPath() {
        return configPath;
    }
}