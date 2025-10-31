package br.assistentediscente.api.main.service;

import br.assistentediscente.api.integrator.exceptions.ai.AINotActiveException;
import br.assistentediscente.api.integrator.exceptions.ai.AIPackageNotFoundException;
import br.assistentediscente.api.integrator.institutions.info.IDiscipline;
import br.assistentediscente.api.main.aiapi.AIApi;
import br.assistentediscente.api.main.dto.ServiceAndActivationNames;
import br.assistentediscente.api.main.model.impl.AIApiData;
import br.assistentediscente.api.main.repository.AIApiDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.List;

@Service
public class AIService {

    @Value("${aiapi.package}")
    private String aiApiPackage;
    private final Environment environment;
    private AIApi aiApi;

    private final AIApiDataRepository aIApiDataRepository;

    public AIService(Environment environment, AIApiDataRepository aIApiDataRepository) {
        this.environment = environment;
        this.aIApiDataRepository = aIApiDataRepository;
    }

    private AIApi getAIApi(){
        if (aiApi == null) {
            AIApiData aiApiData = aIApiDataRepository.findByActiveTrue()
                    .orElseThrow(AINotActiveException::new);

            String aiApiClasspathApi = aiApiData.getClasspathApi();
            String apiKeyEnvPath = aiApiData.getKeyEnvPath();

            try {
                String apiKey = environment.getProperty(apiKeyEnvPath);
                Constructor<?> aiApiConstructor = Class.forName(aiApiPackage + aiApiClasspathApi)
                        .getConstructor(AIApiData.class, String.class);
                return (AIApi) aiApiConstructor.newInstance(aiApiData, apiKey);
            } catch (Exception e) {
                throw new AIPackageNotFoundException(new Object[]{aiApiData.getShortName()});
            }
        }
        return aiApi;
    }

    public String getDisciplineNameFromAIWithDiscipline(List<String> parameters, List<? extends IDiscipline> disciplines) {
        if (disciplines == null || disciplines.isEmpty())
            return "NENHUMA";
        AIApi aiApi = getAIApi();
        return aiApi.guessDisciplineNameFromIDiscipline(parameters.get(0), disciplines);
    }

    public String getServicePluginByActivationName(String activationName, List<ServiceAndActivationNames> serviceAndActivationNames) {
        if (serviceAndActivationNames == null || serviceAndActivationNames.isEmpty()){
            return "NENHUM";
        }
        AIApi aiApi = getAIApi();
        return aiApi.guessServicePluginActivationName(activationName,serviceAndActivationNames);
    }

    public String getItemInList(String itemName, List<String> items) {
        if (items == null || items.isEmpty())
            return "NENHUMA";
        AIApi aiApi = getAIApi();
        return aiApi.guessItemFromItems(itemName, items);
    }
}
