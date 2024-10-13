package br.assistentediscente.api.main.repository;

import br.assistentediscente.api.main.model.impl.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long>,
        JpaSpecificationExecutor<Student> {

    Optional<Student> findByExternalKey(UUID externalKey);
}
