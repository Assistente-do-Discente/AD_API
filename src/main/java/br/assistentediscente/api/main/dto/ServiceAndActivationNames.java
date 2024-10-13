package br.assistentediscente.api.main.dto;

import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;

import java.util.List;

public record ServiceAndActivationNames(
        IServicePlugin service,
        String serviceName,
        List<String> activationNames) {
}
