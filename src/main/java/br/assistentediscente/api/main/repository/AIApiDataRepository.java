package br.assistentediscente.api.main.repository;

import br.assistentediscente.api.main.model.impl.AIApiData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AIApiDataRepository extends JpaRepository<AIApiData, Long> {
    Optional<AIApiData> findByActiveTrue();
}