package br.assistentediscente.api.institutionplugin.ueg.converter;

import br.assistentediscente.api.integrator.converter.IParameterTool;
import br.assistentediscente.api.integrator.enums.ClazzType;
import br.assistentediscente.api.integrator.enums.ParameterType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterTool implements IParameterTool {
    ParameterType type;
    ClazzType clazz;
    String description;
    List<String> possibleValues;
}
