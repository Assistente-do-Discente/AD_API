package br.assistentediscente.api.main.repository;

import br.assistentediscente.api.main.model.impl.AccessData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessDataRepository extends JpaRepository<AccessData, Long> {
}
