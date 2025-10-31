package br.assistentediscente.api.integrator.institutions.info;

@FunctionalInterface
public interface INormalizationMethod {
    String normalize(String parameter);
}
