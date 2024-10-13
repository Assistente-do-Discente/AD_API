package br.assistentediscente.api.integrator.exceptions;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.GENERIC_EXCEPTION_FOR_INSTITUTION;

/**
 * Classe para envio de mensagens da instuição
 */
public class GenericBusinessException extends BusinessException {

    public GenericBusinessException(Object... parameters) {
        super(GENERIC_EXCEPTION_FOR_INSTITUTION, parameters);
    }

    public GenericBusinessException() {
        super(GENERIC_EXCEPTION_FOR_INSTITUTION);
    }

    public GenericBusinessException(String institutionMessage) {
        super(institutionMessage, GENERIC_EXCEPTION_FOR_INSTITUTION.getCode());
    }
}
