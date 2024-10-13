package br.assistentediscente.api.integrator.serviceplugin.parameters;

import br.assistentediscente.api.integrator.enums.ParameterType;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;

public abstract class AParameter {

    public final String getName(){
        return this.getClass().getSimpleName();
    }

    public abstract ParameterType getType();
    public abstract Object getDefaultValue();
    public abstract Object getValueFromInstitution(IBaseInstitutionPlugin institution);
    public abstract Object getObjectValue(String value); //para saber o tipo do valor do parametro

}
