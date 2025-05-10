package dropwizardmcp;

import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Environment;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

public class McpServerBundle implements ConfiguredBundle<HasMcpServerConfiguration> {
    private McpSyncServer syncServer;

    private McpServerBundle() {
        // Use builder() to create an instance
    }

    @Override
    public void run(HasMcpServerConfiguration configuration, Environment environment) throws Exception {
        HttpServletSseServerTransportProvider transportProvider = HttpServletSseServerTransportProvider.builder()
                .objectMapper(environment.getObjectMapper())
                .baseUrl("/mcp")
                .sseEndpoint("/mcp/sse")
                .messageEndpoint("/mcp/message")
                .build();

        environment.servlets().addServlet("mcp", transportProvider).addMapping("/mcp/*");

        McpServerConfiguration mcpServerConfiguration = configuration.getMcpServerConfiguration();
        syncServer = McpServer.sync(transportProvider)
                .serverInfo(mcpServerConfiguration.getServerName(), mcpServerConfiguration.getServerVersion())
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(mcpServerConfiguration.hasTools())
                        .resources(false, false)
                        .prompts(false)
                        .build())
                .build();
    }

    public static McpServerBundle.Builder builder() {
        return new McpServerBundle.Builder();
    }

    public void addTool(Tool<?, ?> tool) {
        McpServerFeatures.SyncToolSpecification syncToolSpecification =
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                tool.getName(),
                                tool.getDescription(),
                                tool.getSchema()
                        ),
                        tool::applyInternal
                );

        syncServer.addTool(syncToolSpecification);
    }

    public static class Builder {
        private Builder() {
            // Private constructor to prevent instantiation
        }
        public McpServerBundle build() {
            return new McpServerBundle();
        }
    }
}
