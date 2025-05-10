package demo;

import dropwizardmcp.HasMcpServerConfiguration;
import dropwizardmcp.McpServerConfiguration;
import io.dropwizard.core.Configuration;

public final class DemoConfiguration extends Configuration implements HasMcpServerConfiguration {

    public McpServerConfiguration mcpServerConfiguration;

    public DemoConfiguration() {
        mcpServerConfiguration = new McpServerConfiguration();
        mcpServerConfiguration.setServerName("dropwizard-mcp-server");
        mcpServerConfiguration.setServerVersion("0.0.1");
        mcpServerConfiguration.setHasTools(true);
    }

    @Override
    public McpServerConfiguration getMcpServerConfiguration() {
        return mcpServerConfiguration;
    }
}
