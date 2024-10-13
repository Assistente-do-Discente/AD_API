package br.assistentediscente.api.integrator.exceptions.serviceplugin;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_MANDATORY_PARAMETER_NOT_FILLED;

public class MandatoryParameterNotFilled extends BusinessException {

    public MandatoryParameterNotFilled(Object... parameters) {
        super(ERROR_MANDATORY_PARAMETER_NOT_FILLED, parameters);
    }

    public MandatoryParameterNotFilled() {
        super(ERROR_MANDATORY_PARAMETER_NOT_FILLED);
    }

    public MandatoryParameterNotFilled(String message) {
        super(message, ERROR_MANDATORY_PARAMETER_NOT_FILLED.getCode());
    }
}
