package br.assistentediscente.api.main.service;

import br.assistentediscente.api.integrator.exceptions.student.StudentNotFoundException;
import br.assistentediscente.api.integrator.institutions.KeyValue;
import br.assistentediscente.api.main.model.IStudent;
import br.assistentediscente.api.main.model.impl.Institution;
import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AccessDataService accessDataService;

    public StudentService(StudentRepository studentRepository, AccessDataService accessDataService) {
        this.studentRepository = studentRepository;
        this.accessDataService = accessDataService;
    }

    public Student create(List<KeyValue> keyValueList, Institution institution){

        Student student =  new Student();
        student.setExternalKey(UUID.randomUUID());
        student.setInstitution(institution);

        student = studentRepository.saveAndFlush(student);
        accessDataService.saveAccessData(keyValueList, student);

        return studentRepository.findById(student.getId()).orElseThrow(StudentNotFoundException::new);
    }

    public IStudent findByExternalKey(UUID uuid) {
        return studentRepository.findByExternalKey(uuid)
                .orElseThrow(StudentNotFoundException::new);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public void refreshAccessData(Student student, List<KeyValue> refreshedAccessData) {
        accessDataService.saveAccessData(refreshedAccessData, student);
    }

}
