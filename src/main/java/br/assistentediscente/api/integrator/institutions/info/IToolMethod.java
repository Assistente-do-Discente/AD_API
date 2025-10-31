package br.assistentediscente.api.integrator.institutions.info;

import br.assistentediscente.api.integrator.converter.IResponseTool;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

@FunctionalInterface
public interface IToolMethod {
    IResponseTool execute(Map<String, String> parameters) throws JsonProcessingException;
}
