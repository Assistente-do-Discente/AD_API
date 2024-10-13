package br.assistentediscente.api.integrator.exceptions.param;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_PARAMETER_NOT_FOUND;

public class ParamNotFoundException extends BusinessException {


    public ParamNotFoundException(Object... parameters) {
        super(ERROR_PARAMETER_NOT_FOUND, parameters);
    }

    public ParamNotFoundException() {
        super(ERROR_PARAMETER_NOT_FOUND);
    }

    public ParamNotFoundException(String message) {
        super(message, ERROR_PARAMETER_NOT_FOUND.getCode());
    }
}
