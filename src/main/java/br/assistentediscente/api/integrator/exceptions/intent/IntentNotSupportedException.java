package br.assistentediscente.api.integrator.exceptions.intent;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_INTENT_NOT_SUPPORTED;

public class IntentNotSupportedException extends BusinessException {

    public IntentNotSupportedException(Object... parameters) {
        super(ERROR_INTENT_NOT_SUPPORTED, parameters);
    }

    public IntentNotSupportedException() {
        super(ERROR_INTENT_NOT_SUPPORTED);
    }

    public IntentNotSupportedException(String institutionMessage) {
        super(institutionMessage, ERROR_INTENT_NOT_SUPPORTED.getCode());
    }
}
