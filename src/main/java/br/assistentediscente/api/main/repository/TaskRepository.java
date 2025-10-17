package br.assistentediscente.api.main.repository;

import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.model.impl.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Boolean existsByIdAndStudent(Long id, Student student);
    List<Task> findAllByStudent(Student student);
    List<Task> findTaskByStudentAndDate(Student student, LocalDateTime date);
}

