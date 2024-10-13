package br.assistentediscente.api.main.aiapi.maritalk_api;

import br.assistentediscente.api.integrator.institutions.info.IDiscipline;
import br.assistentediscente.api.main.aiapi.AIApi;
import br.assistentediscente.api.main.dto.ServiceAndActivationNames;
import br.assistentediscente.api.main.model.impl.AIApiData;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class MaritalkApi extends AIApi {

    private final Gson gson = new Gson();

    public MaritalkApi(AIApiData aiApiData, String apiKey) {
        super(aiApiData, apiKey);
    }

    @Override
    public String guessDisciplineNameFromIDiscipline(String disciplineIntent, List<? extends IDiscipline> disciplines) {
        String message = getDisciplineNamesMessage(disciplineIntent,
                disciplines.stream().map(IDiscipline::getDisciplineName).toList());
        return sendMessageAndGetApiResponse(message);
    }

    @Override
    public String guessServicePluginActivationName(String activationName, List<ServiceAndActivationNames> serviceAndActivationNames) {
        String message = getServiceActivationNameMessage(activationName, serviceAndActivationNames);
        return sendMessageAndGetApiResponse(message);
    }


    private String sendMessageAndGetApiResponse(String message) {
        String params = aiApiData.getParamsApi().replace("?", message);
        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(aiApiData.getUrlApi()))
                    .header("Authorization","Key "+apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(params))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> httpResponse =
                    httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            return getResponse(httpResponse);
        } catch (Throwable error) {
            throw new RuntimeException(error);
        }
    }

    private String getResponse(HttpResponse<String> httpResponse) {
        MaritalkResponse response = gson.fromJson(JsonParser.parseString(httpResponse.body()), MaritalkResponse.class);
        return response.getResponse().trim();
    }
}
