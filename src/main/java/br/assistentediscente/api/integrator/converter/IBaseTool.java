package br.assistentediscente.api.integrator.converter;

import br.assistentediscente.api.integrator.institutions.info.IToolMethod;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;
import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public interface IBaseTool {
    String getName();
    String getDescription();
    Boolean getHighConfirmation();
    Map<String, AParameter> getParameters();

    @JsonIgnore
    IToolMethod getExecuteMethod();
    @JsonIgnore
    Class<? extends IServicePlugin> getServiceClass();
}
