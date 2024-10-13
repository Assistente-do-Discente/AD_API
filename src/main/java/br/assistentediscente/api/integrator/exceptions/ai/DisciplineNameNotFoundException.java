package br.assistentediscente.api.integrator.exceptions.ai;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_DISCIPLINE_NAME_NOT_FOUND;

public class DisciplineNameNotFoundException extends BusinessException {


    public DisciplineNameNotFoundException(Object... parameters) {
        super(ERROR_DISCIPLINE_NAME_NOT_FOUND, parameters);
    }

    public DisciplineNameNotFoundException() {
        super(ERROR_DISCIPLINE_NAME_NOT_FOUND);
    }

    public DisciplineNameNotFoundException(String message) {
        super(message, ERROR_DISCIPLINE_NAME_NOT_FOUND.getCode());
    }
}
