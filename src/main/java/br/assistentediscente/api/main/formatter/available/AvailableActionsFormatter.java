package br.assistentediscente.api.main.formatter.available;

import br.assistentediscente.api.main.formatter.BaseFormatter;
import org.springframework.context.MessageSource;

import java.util.List;

import static br.assistentediscente.api.main.formatter.available.AvailableActionsResponse.AVAILABLE_ACTIONS_RESPONSE;

public class AvailableActionsFormatter extends BaseFormatter {

    public AvailableActionsFormatter(MessageSource messageSource) {
        super(messageSource);
    }

    public String doResponse(List<String> institutionServicesNames){

        StringBuilder builder = new StringBuilder();

        builder.append("Consultar suas aulas a partir do dia ou nome da disciplina, " +
                "Consultar todas as suas notas de um semestre, " +
                "Consultar suas notas em uma de suas discplinas, " +
                "Consultar a quantidade de faltas em uma disciplina que está cursando, " +
                "Consultar a sua média geral do curso,");
        for (String institutionServiceName : institutionServicesNames) {
            builder.append(institutionServiceName).append(", ");
        }
        if (builder.toString().endsWith(", ")) {
            builder.delete(builder.length() - 2, builder.length());
        }

        return getMessage(AVAILABLE_ACTIONS_RESPONSE, builder.toString(), "Quais são as suas aulas de hoje");
    }
}
