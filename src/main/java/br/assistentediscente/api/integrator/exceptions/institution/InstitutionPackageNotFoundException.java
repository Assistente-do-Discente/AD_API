package br.assistentediscente.api.integrator.exceptions.institution;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_INSTITUTION_PLUGIN_NOT_FOUND;

public class InstitutionPackageNotFoundException extends BusinessException {

    public InstitutionPackageNotFoundException(Object... parameters) {
        super(ERROR_INSTITUTION_PLUGIN_NOT_FOUND, parameters);
    }

    public InstitutionPackageNotFoundException() {
        super(ERROR_INSTITUTION_PLUGIN_NOT_FOUND);
    }

    public InstitutionPackageNotFoundException(String message) {
        super(message, ERROR_INSTITUTION_PLUGIN_NOT_FOUND.getCode());
    }
}
