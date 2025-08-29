package br.assistentediscente.api.institutionplugin.ueg.serviceplugin.parameter;

import br.assistentediscente.api.integrator.enums.ClazzType;
import br.assistentediscente.api.integrator.enums.ParameterType;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;

import java.util.List;

public class StudentParameter implements AParameter {

    @Override
    public ParameterType getType() {
        return ParameterType.AUTO;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public Object getValueFromInstitution(IBaseInstitutionPlugin institution) {
        return institution.getStudentData();
    }

    @Override
    public Object getObjectValue(String value) {
        return null;
    }

    @Override
    public ClazzType getClazz() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getPossibleValues() {
        return null;
    }

}
