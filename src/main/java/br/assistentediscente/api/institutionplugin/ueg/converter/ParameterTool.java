package br.assistentediscente.api.institutionplugin.ueg.converter;

import br.assistentediscente.api.integrator.enums.ClazzType;
import br.assistentediscente.api.integrator.enums.ParameterType;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterTool implements AParameter {
    ParameterType type;
    ClazzType clazz;
    String description;
    List<String> possibleValues;

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public Object getValueFromInstitution(IBaseInstitutionPlugin institution) {
        return null;
    }

    @Override
    public Object getObjectValue(String value) {
        return null;
    }

    public static ParameterTool stringParam(String description, ParameterType type) {
        return ParameterTool.builder()
                .clazz(ClazzType.STRING)
                .type(type)
                .description(description)
                .build();
    }

    public static <E> ParameterTool enumParam(String description, E[] values, Function<E, String> toLabel) {
        List<String> possibleValues = Arrays.stream(values).map(toLabel).toList();
        return ParameterTool.builder()
                .clazz(ClazzType.ENUM)
                .type(ParameterType.MANDATORY)
                .description(description)
                .possibleValues(possibleValues)
                .build();
    }
}
