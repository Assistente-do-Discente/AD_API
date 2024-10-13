package br.assistentediscente.api.main.scheduled;

import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.service.ResponseService;
import br.assistentediscente.api.main.service.StudentService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@EnableScheduling
public class RefreshAcessData {

    private final StudentService studentService;
    private final ResponseService responseService;

    public RefreshAcessData(StudentService studentService, ResponseService responseService) {
        this.studentService = studentService;
        this.responseService = responseService;
    }

    private final long SECOND  = 1000;
    private final long MINUTE = SECOND * 60;

    @Scheduled(fixedDelay = MINUTE*20)
    public void refreshStudentsAccessData() {
        System.out.println("\nINICIANDO REFRESH DOS COOKIES DE USUARIOS \nHORA: " + LocalTime.now()+"\n");
        List<Student> students = studentService.findAll();
        for(Student student : students) {
            try {
                responseService.getRefreshedAccessData(student);
            }catch (Exception e){
                System.out.println("\nERRO REFRESH DOS COOKIES DO USUARIO: "+student.getId().toString() +
                        "\nHORA: " + LocalTime.now()+"\n");
            }
        }
        System.out.println("\nENCERRANDO REFRESH DOS COOKIES DE USUARIOS \nHORA: " + LocalTime.now()+"\n");

    }
}
