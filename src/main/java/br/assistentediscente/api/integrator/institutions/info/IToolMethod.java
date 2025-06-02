package br.assistentediscente.api.integrator.institutions.info;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

@FunctionalInterface
public interface IToolMethod {
    Map<String, String> execute(Map<String, String> parameters) throws JsonProcessingException;
}
