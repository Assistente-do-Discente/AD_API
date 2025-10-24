package br.assistentediscente.api.integrator.institutions.info;

import java.util.List;

@FunctionalInterface
public interface IPossibleValuesMethod {
    List<String> getPossibleValues();
}
