package br.assistentediscente.api.integrator.converter;

import br.assistentediscente.api.integrator.institutions.info.IToolMethod;

import java.util.Map;

public interface IBaseTool {
    String getName();
    String getDescription();
    Map<String, IParameterTool> getParameters();
    IToolMethod getExecuteMethod();
}
