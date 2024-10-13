package br.assistentediscente.api.integrator.serviceplugin.parameters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParameterValue {

    private AParameter parameter;
    private Object value;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterValue that = (ParameterValue) o;
        return Objects.equals(parameter.getName(), that.parameter.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter.getName());
    }
}
