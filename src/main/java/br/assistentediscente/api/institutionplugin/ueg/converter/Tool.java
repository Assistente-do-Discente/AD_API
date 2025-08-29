package br.assistentediscente.api.institutionplugin.ueg.converter;

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
public class Tool implements IBaseTool {
    String name;
    String description;
    Map<String, AParameter> parameters;
    IToolMethod executeMethod;

    @Override
    public Class<? extends IServicePlugin> getServiceClass() {
        return null;
    }

    public static Tool tool(String name, String description, IToolMethod executeMethod) {
        return Tool.builder()
                .name(name)
                .description(description)
                .executeMethod(executeMethod)
                .build();
    }

    public static Tool tool(String name, String description, IToolMethod executeMethod, Map<String, AParameter> parameters) {
        return Tool.builder()
                .name(name)
                .description(description)
                .parameters(parameters)
                .executeMethod(executeMethod)
                .build();
    }
}
