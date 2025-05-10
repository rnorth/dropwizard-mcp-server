package dropwizardmcp;

public class McpServerConfiguration {
    private String serverName = "dropwizard-mcp-server";
    private String serverVersion = "0.0.1";
    private boolean hasTools = false;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public boolean hasTools() {
        return hasTools;
    }

    public void setHasTools(boolean hasTools) {
        this.hasTools = hasTools;
    }
}
