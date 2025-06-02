package br.assistentediscente.api.integrator.converter;

import br.assistentediscente.api.integrator.enums.ClazzType;
import br.assistentediscente.api.integrator.enums.ParameterType;

import java.util.List;

public interface IParameterTool {
    ParameterType getType();
    ClazzType getClazz();
    String getDescription();
    List<String> getPossibleValues();
}