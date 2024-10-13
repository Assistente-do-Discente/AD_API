package br.assistentediscente.api.integrator.exceptions.serviceplugin;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_COULD_NOT_EXECUTE_SERVICE;

public class ServiceCouldNotExecute extends BusinessException {

  public ServiceCouldNotExecute(Object... parameters) {
    super(ERROR_COULD_NOT_EXECUTE_SERVICE, parameters);
  }

  public ServiceCouldNotExecute() {
    super(ERROR_COULD_NOT_EXECUTE_SERVICE);
  }

  public ServiceCouldNotExecute(String message) {
    super(message, ERROR_COULD_NOT_EXECUTE_SERVICE.getCode());
  }
}