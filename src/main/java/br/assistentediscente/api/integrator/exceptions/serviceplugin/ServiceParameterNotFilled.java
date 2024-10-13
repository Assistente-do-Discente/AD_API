package br.assistentediscente.api.integrator.exceptions.serviceplugin;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_SERVICE_PARAMETER_NOT_FILLED;

public class ServiceParameterNotFilled extends BusinessException {

    public ServiceParameterNotFilled(Object... parameters) {
        super(ERROR_SERVICE_PARAMETER_NOT_FILLED, parameters);
    }

    public ServiceParameterNotFilled() {
        super(ERROR_SERVICE_PARAMETER_NOT_FILLED);
    }

    public ServiceParameterNotFilled(String message) {
        super(message, ERROR_SERVICE_PARAMETER_NOT_FILLED.getCode());
    }
}
