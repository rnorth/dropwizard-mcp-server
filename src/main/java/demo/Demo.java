package demo;

import dropwizardmcp.McpServerBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class Demo extends Application<DemoConfiguration> {

    private McpServerBundle mcpServerBundle;

    public static void main(String[] args) throws Exception {
        new Demo().run(args);
    }

    @Override
    public void initialize(Bootstrap<DemoConfiguration> bootstrap) {
        WeatherForecastTool weatherForecastTool = new WeatherForecastTool();

        mcpServerBundle = McpServerBundle.builder()
                .build();

        bootstrap.addBundle(mcpServerBundle);
    }

    @Override
    public void run(DemoConfiguration configuration, Environment environment) throws Exception {
        mcpServerBundle.addTool(new WeatherForecastTool());
    }
}
