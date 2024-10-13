package br.assistentediscente.api.integrator.exceptions.files;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_COULDNT_DELETE_FILE;

public class ErrorCouldNotDeleteFile extends BusinessException {


    public ErrorCouldNotDeleteFile(Object... parameters) {
        super(ERROR_COULDNT_DELETE_FILE, parameters);
    }

    public ErrorCouldNotDeleteFile() {
        super(ERROR_COULDNT_DELETE_FILE);
    }

    public ErrorCouldNotDeleteFile(String message) {
        super(message, ERROR_COULDNT_DELETE_FILE.getCode());
    }
}
