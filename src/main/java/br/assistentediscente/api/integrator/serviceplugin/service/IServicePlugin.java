package br.assistentediscente.api.integrator.serviceplugin.service;

import br.assistentediscente.api.integrator.exceptions.BusinessException;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;
import br.assistentediscente.api.integrator.plataformeservice.IPlataformService;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;
import br.assistentediscente.api.integrator.serviceplugin.parameters.ParameterValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IServicePlugin {

    /**
     * Valores que serão utilizados no momentos de invocação do serviço de acordo com o que o usuario passar
     * O primeiro item do Set será passado para o usuario quando perguntar o que se pode fazer no sistema
     */
    List<String> getActivationName();

    List<AParameter> getParameters();

    Set<ParameterValue> getParameterValues(IBaseInstitutionPlugin institution,
                                           Map<String, String> parameters);

    String doService(IBaseInstitutionPlugin institution, Set<ParameterValue> parameterValues,
                     IPlataformService plataformService) throws BusinessException;

}
