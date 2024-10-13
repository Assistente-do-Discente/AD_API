package br.assistentediscente.api.main.model;

import br.assistentediscente.api.integrator.institutions.KeyValue;
import br.assistentediscente.api.main.model.impl.AccessData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IStudent {

    Long getId();
    UUID getExternalKey();
    Set<AccessData> getAccessData();
    void setAccessData(Set<AccessData> accessData);
    IInstitution getEducationalInstitution();
    List<KeyValue> getKeyValueList();

}
