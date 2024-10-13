package br.assistentediscente.api.main.formatter.overallaverage;

import br.assistentediscente.api.integrator.institutions.info.IAcademicData;
import br.assistentediscente.api.main.formatter.BaseFormatter;
import org.springframework.context.MessageSource;

import static br.assistentediscente.api.main.formatter.overallaverage.OverallAverageResponse.ERROR_OVERALL_RESPONSE;
import static br.assistentediscente.api.main.formatter.overallaverage.OverallAverageResponse.START_OVERALL_RESPONSE;

public class OverallAverageFormatter extends BaseFormatter {

    public OverallAverageFormatter(MessageSource messageSource) {
        super(messageSource);
    }

    public String doResponse(IAcademicData academicData) {
        if (academicData == null) {
            return getMessage(ERROR_OVERALL_RESPONSE);
        }

        return getMessage(START_OVERALL_RESPONSE, academicData.getCourseName(), academicData.getOverallAverage().toString());
    }
}
