package br.assistentediscente.api.integrator.exceptions.serviceplugin;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_SERVICE_PLUGIN_NOT_FOUND;

public class ServicePluginNotFound extends BusinessException {

  public ServicePluginNotFound(Object... parameters) {
    super(ERROR_SERVICE_PLUGIN_NOT_FOUND, parameters);
  }

  public ServicePluginNotFound() {
    super(ERROR_SERVICE_PLUGIN_NOT_FOUND);
  }

  public ServicePluginNotFound(String message) {
    super(message, ERROR_SERVICE_PLUGIN_NOT_FOUND.getCode());
  }
}