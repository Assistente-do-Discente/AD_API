package br.assistentediscente.api.integrator.exceptions.serviceplugin;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_PARAMETER_TYPE_NOT_SUPPORTED;

public class ParameterTypeNotSupported extends BusinessException {

    public ParameterTypeNotSupported(Object... parameters) {
        super(ERROR_PARAMETER_TYPE_NOT_SUPPORTED, parameters);
    }

    public ParameterTypeNotSupported() {
        super(ERROR_PARAMETER_TYPE_NOT_SUPPORTED);
    }

    public ParameterTypeNotSupported(String message) {
        super(message, ERROR_PARAMETER_TYPE_NOT_SUPPORTED.getCode());
    }
}
