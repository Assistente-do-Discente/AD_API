package br.assistentediscente.api.integrator.exceptions;

import br.assistentediscente.api.integrator.enums.BusinessErrorMessage;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 5326024408089356509L;

    private String institutionMessage;
    private BusinessErrorMessage businessErrorMessage;
    private Object[] parameters;
    private Integer code;

    protected BusinessException(final BusinessErrorMessage businessErrorMessage, final Object... parameters) {
        this.businessErrorMessage = businessErrorMessage;
        this.parameters = parameters;
        this.code = businessErrorMessage.getCode();
    }

    protected BusinessException(final BusinessErrorMessage businessErrorMessage) {
        this.businessErrorMessage = businessErrorMessage;
        this.code = businessErrorMessage.getCode();
    }

    protected BusinessException(final String institutionMessage, final Integer code) {
        this.institutionMessage = institutionMessage;
        this.code = code;
    }
    @Override
    public String getMessage() {
        if (institutionMessage != null)
            return institutionMessage;

        return businessErrorMessage.getMessage();
    }

    public boolean hasParameters() {
        return parameters != null && parameters.length > 0;
    }

    public boolean hasBusinessMessage() {
        return businessErrorMessage != null;
    }

    public Integer getCode() {
        return code;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
