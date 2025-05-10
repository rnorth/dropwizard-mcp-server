package dropwizardmcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.hibernate.validator.HibernateValidator;

import java.util.List;
import java.util.Map;

public abstract class Tool<T, O> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected abstract String getName();

    protected abstract String getDescription();

    protected abstract Class<T> getInputClass();

    protected abstract O apply(T input);

    String getSchema() {
        JacksonModule jacksonModule = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED);
        SchemaGeneratorConfig config = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
                .with(Option.INLINE_ALL_SCHEMAS)
                .with(jacksonModule)
                .build();
        SchemaGenerator generator = new SchemaGenerator(config);

        JsonNode jsonSchema = generator.generateSchema(getInputClass());

        return jsonSchema.toPrettyString();
    }

    McpSchema.CallToolResult applyInternal(McpSyncServerExchange mcpSyncServerExchange, Map<String, Object> stringObjectMap) {
        T input;
        try {
            input = OBJECT_MAPPER.convertValue(stringObjectMap, this.getInputClass());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error deserializing input", e);
        }

        // use hibernate validator to validate the input
        var validatorFactory = jakarta.validation.Validation.byProvider(HibernateValidator.class).configure();
        try (var validatorFactory1 = validatorFactory.buildValidatorFactory()) {
            var validator = validatorFactory1.getValidator();
            var violations = validator.validate(input);
            if (!violations.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Validation errors: ");
                for (var violation : violations) {
                    errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
                }
                return new McpSchema.CallToolResult(errorMessage.toString(), true);
            }
        }

        O output;
        try {
            output = apply(input);
        } catch (Exception e) {
            return new McpSchema.CallToolResult("Error applying tool: " + e.getMessage(), true);
        }

        String serializedOutput;
        try {
            serializedOutput = OBJECT_MAPPER.writeValueAsString(output);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(serializedOutput)), false);
    }
}
