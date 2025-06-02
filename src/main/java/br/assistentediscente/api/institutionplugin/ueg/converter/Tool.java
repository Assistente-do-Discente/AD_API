package br.assistentediscente.api.institutionplugin.ueg.converter;

import br.assistentediscente.api.integrator.converter.IBaseTool;
import br.assistentediscente.api.integrator.converter.IParameterTool;
import br.assistentediscente.api.integrator.institutions.info.IToolMethod;
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
    Map<String, IParameterTool> parameters;
    IToolMethod executeMethod;

}
