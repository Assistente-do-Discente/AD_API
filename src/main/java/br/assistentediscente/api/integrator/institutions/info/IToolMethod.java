package br.assistentediscente.api.integrator.institutions.info;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

@FunctionalInterface
public interface IToolMethod {
    Object execute(Map<String, String> parameters) throws JsonProcessingException;
}
