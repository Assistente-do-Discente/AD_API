package br.assistentediscente.api.integrator.exceptions.student;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_STUDENT_NOT_AUTHENTICATED;

public class StudentNotAuthenticatedException extends BusinessException {

    public StudentNotAuthenticatedException(Object... parameters) {
        super(ERROR_STUDENT_NOT_AUTHENTICATED, parameters);
    }

    public StudentNotAuthenticatedException() {
        super(ERROR_STUDENT_NOT_AUTHENTICATED);
    }

    public StudentNotAuthenticatedException(String message) {
        super(message, ERROR_STUDENT_NOT_AUTHENTICATED.getCode());
    }
}
