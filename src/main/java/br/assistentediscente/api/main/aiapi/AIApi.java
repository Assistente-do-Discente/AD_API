package br.assistentediscente.api.main.aiapi;

import br.assistentediscente.api.main.dto.ServiceAndActivationNames;
import br.assistentediscente.api.main.model.impl.AIApiData;

import java.util.List;

public abstract class AIApi implements IAIApi{

    protected AIApiData aiApiData;
    protected String apiKey;

    public AIApi(AIApiData aiApiData, String apiKey) {
        this.aiApiData = aiApiData;
        this.apiKey = apiKey;
    }

    protected String getDisciplineNamesMessage(String disciplineIntentList, List<String> disciplinesNames) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startDisciplineNameQuestion);
        for (String disciplineName : disciplinesNames) {
            stringBuilder.append(disciplineName);
            stringBuilder.append(", ");
        }
        stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.length(), ".");
        stringBuilder.append(endDisciplineNameQuestion.replace("?", disciplineIntentList.toUpperCase().trim()));
        return stringBuilder.toString().trim();
    }

    protected String getServiceActivationNameMessage(String activationName, List<ServiceAndActivationNames> serviceAndActivationNames) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startServiceActivationNameQuestion);

        for (ServiceAndActivationNames serviceAndActivationName : serviceAndActivationNames) {
            String names = "'" + String.join("', '", serviceAndActivationName.activationNames()) + "'";

            String serviceMessage = serviceActivationProgressNameQuestion
                    .replace("{0}", serviceAndActivationName.serviceName())
                    .replace("{1}", names);

            stringBuilder.append(serviceMessage);
        }

        stringBuilder.setLength(stringBuilder.length() - 2);


        stringBuilder.append(endServiceActivationNameQuestion.replace("?", activationName.toLowerCase().trim()));

        return stringBuilder.toString().trim();
    }

    protected String getItemsMessage(String item, List<String> items) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startItemQuestion);
        for (String itemTemp : items) {
            stringBuilder.append(itemTemp);
            stringBuilder.append(", ");
        }
        stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.length(), ".");
        stringBuilder.append(endItemQuestion.replace("?", item.toUpperCase().trim()));
        return stringBuilder.toString().trim();
    }
}
