package br.assistentediscente.api.integrator.exceptions.student;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_SESSION_EXPIRES;

public class StudentNotFoundException extends BusinessException {

    public StudentNotFoundException() {
        super(ERROR_SESSION_EXPIRES);
    }

    public StudentNotFoundException(Object... parameters){
        super(ERROR_SESSION_EXPIRES, parameters);
    }

    public StudentNotFoundException(String message) {
        super(message,ERROR_SESSION_EXPIRES.getCode());
    }
}
