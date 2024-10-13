package br.assistentediscente.api.main.service;

import br.assistentediscente.api.integrator.institutions.KeyValue;
import br.assistentediscente.api.main.model.impl.AccessData;
import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.repository.AccessDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessDataService {

    private final AccessDataRepository accessDataRepository;

    public AccessDataService(AccessDataRepository accessDataRepository) {
        this.accessDataRepository = accessDataRepository;
    }

    public void saveAccessData(List<KeyValue> keyValueList, Student student) {
        for(KeyValue keyValue : keyValueList) {
            AccessData accessData = new AccessData();
            accessData.setKey(keyValue.getKey());
            accessData.setValue(keyValue.getValue());
            accessData.setStudent(student);
            accessDataRepository.saveAndFlush(accessData);
        }
    }
}
