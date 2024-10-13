package br.assistentediscente.api.main.repository;

import br.assistentediscente.api.main.model.impl.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long>{

    Optional<Institution> findByShortName(@NonNull String name);

}