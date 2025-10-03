package br.assistentediscente.api.main.service;

import br.assistentediscente.api.integrator.exceptions.institution.InstitutionNotFoundException;
import br.assistentediscente.api.main.dto.InstitutionLoginFieldsDTO;
import br.assistentediscente.api.main.model.impl.Institution;
import br.assistentediscente.api.main.repository.InstitutionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<Institution> getAllInstitution() {
        return institutionRepository.findAll();
    }

    public Institution getInstitutionByInstitutionName(String institutionName) {

        return institutionRepository.findByShortName(institutionName)
                .orElseThrow(InstitutionNotFoundException::new);
    }

    public String getInstitutionSaudationPhraseByInstitutionName(String institutionShortName) {
        Institution institution = institutionRepository.findByShortName(institutionShortName).orElse(null);

        if (Objects.nonNull(institution))
            return institution.getSaudationPhrase();

        throw  new InstitutionNotFoundException();
    }

    public InstitutionLoginFieldsDTO getInstitutionLoginFields(String institutionShortName) {

        Institution institution = institutionRepository.findByShortName(institutionShortName).orElse(null);
        if (Objects.nonNull(institution))
            return new InstitutionLoginFieldsDTO(institution.getSaudationPhrase(),
                    institution.getUsernameFieldName(), institution.getPasswordFieldName(), HttpStatus.OK.value());

        throw  new InstitutionNotFoundException();
    }
}
