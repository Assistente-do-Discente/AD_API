package br.assistentediscente.api.main.repository;

import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.model.impl.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Boolean existsByIdAndStudent(Long id, Student student);
    Optional<Task> findByIdAndStudent(Long id, Student student);
    List<Task> findAllByStudent(Student student);
    List<Task> findTaskByStudentAndDateBetween(Student student, LocalDateTime start, LocalDateTime end);
}

