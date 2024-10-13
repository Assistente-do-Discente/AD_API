package br.assistentediscente.api.integrator.exceptions.ai;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_AI_PLUGIN_NOT_FOUND;

public class AIPackageNotFoundException extends BusinessException {


    public AIPackageNotFoundException(Object... parameters) {
        super(ERROR_AI_PLUGIN_NOT_FOUND, parameters);
    }

    public AIPackageNotFoundException() {
        super(ERROR_AI_PLUGIN_NOT_FOUND);
    }

    public AIPackageNotFoundException(String message) {
        super(message, ERROR_AI_PLUGIN_NOT_FOUND.getCode());
    }
}
