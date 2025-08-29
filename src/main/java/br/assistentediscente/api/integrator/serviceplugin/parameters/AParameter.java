package br.assistentediscente.api.integrator.serviceplugin.parameters;

import br.assistentediscente.api.integrator.enums.ClazzType;
import br.assistentediscente.api.integrator.enums.ParameterType;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;

import java.util.List;

public interface AParameter {

    default String getName(){
        return this.getClass().getSimpleName();
    }

    ParameterType getType();
    Object getDefaultValue();
    Object getValueFromInstitution(IBaseInstitutionPlugin institution);
    Object getObjectValue(String value); //para saber o tipo do valor do parametro

    ClazzType getClazz();
    String getDescription();
    List<String> getPossibleValues();

}
