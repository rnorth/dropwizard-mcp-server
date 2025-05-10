package demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import dropwizardmcp.Tool;

public class WeatherForecastTool extends Tool<WeatherForecastTool.Input, WeatherForecastTool.Output> {
    @Override
    protected String getDescription() {
        return "gives today's weather forecast for a given location";
    }

    @Override
    protected String getName() {
        return "weather-forecast";
    }

    @Override
    protected Output apply(Input input) {
        return new Output(input.location, "sunny", 25);
    }

    @Override
    protected Class<Input> getInputClass() {
        return Input.class;
    }

    public record Input(@JsonProperty(required = true) String location) {
    }

    public record Output(String location, String type, int temperature) {
    }
}
