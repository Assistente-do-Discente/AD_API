package br.assistentediscente.api.main.dto;

import br.assistentediscente.api.integrator.converter.IBaseTool;
import br.assistentediscente.api.integrator.institutions.info.IToolMethod;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;
import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDTO implements IBaseTool {
    String name;
    String description;
    Boolean highConfirmation;
    Map<String, AParameter> parameters;
    IToolMethod executeMethod;
    Class<? extends IServicePlugin> serviceClass;
    Boolean authenticationRequired;

    public static ToolDTO tool(String name, String description, IToolMethod executeMethod) {
        return tool(name, description, true, executeMethod, null);
    }

    public static ToolDTO tool(String name, String description, boolean authenticationRequired, IToolMethod executeMethod) {
        return tool(name, description, authenticationRequired, executeMethod, null);
    }

    public static ToolDTO tool(String name, String description, IToolMethod executeMethod, Map<String, AParameter> parameters) {
        return tool(name, description, true, executeMethod, parameters);
    }

    public static ToolDTO tool(String name, String description, boolean authenticationRequired, IToolMethod executeMethod, Map<String, AParameter> parameters) {
        return ToolDTO.builder()
                .name(name)
                .description(description)
                .parameters(parameters)
                .executeMethod(executeMethod)
                .highConfirmation(false)
                .authenticationRequired(authenticationRequired)
                .build();
    }
}