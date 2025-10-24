package br.assistentediscente.api.main.repository;

import br.assistentediscente.api.main.model.impl.Planner;
import br.assistentediscente.api.main.model.impl.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {
    Boolean existsByIdAndStudent(Long id, Student student);
    List<Planner> findAllByStudent(Student student);
}
