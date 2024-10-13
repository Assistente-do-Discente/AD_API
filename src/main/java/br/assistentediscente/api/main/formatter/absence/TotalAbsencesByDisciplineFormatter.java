package br.assistentediscente.api.main.formatter.absence;

import br.assistentediscente.api.integrator.exceptions.ai.DisciplineNameNotFoundException;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineAbsence;
import br.assistentediscente.api.main.formatter.BaseFormatter;
import org.springframework.context.MessageSource;

import java.util.List;

import static br.assistentediscente.api.main.formatter.absence.AbsenceResponse.*;

public class TotalAbsencesByDisciplineFormatter extends BaseFormatter {

    public TotalAbsencesByDisciplineFormatter(MessageSource messageSource) {
        super(messageSource);
    }

    public String doResponse(String disciplineToGetAbsence, List<IDisciplineAbsence> disciplinesToAbsences) {

        IDisciplineAbsence disciplineAbsence = disciplinesToAbsences.stream()
                .filter(discipline ->  discipline.getDisciplineName().trim().equalsIgnoreCase(disciplineToGetAbsence)
                ).findFirst().orElse(null);

        if (disciplineAbsence == null) {
            throw new DisciplineNameNotFoundException(disciplineToGetAbsence);
        }
        String disciplineName = switchRomanNumberToNumber(disciplineAbsence.getDisciplineName());

        if (zeroAbsence(disciplineAbsence)){
            return getMessage(RESPONSE_ZERO_ABSENCE, disciplineName);
        }

        if(hasExcusedAbsence(disciplineAbsence)){
            return getMessage(RESPONSE_TOTAL_ABSENCE_WITH_EXCUSED, disciplineAbsence.getTotalAbsence(),
                    disciplineName, disciplineAbsence.getTotalExcusedAbsences());
        }

        return getMessage(RESPONSE_TOTAL_ABSENCE, disciplineAbsence.getTotalAbsence(), disciplineName);
    }

    private boolean hasExcusedAbsence(IDisciplineAbsence disciplineAbsence) {
        return disciplineAbsence.getTotalExcusedAbsences() != null  &&
                disciplineAbsence.getTotalExcusedAbsences() > 0L;
    }

    private boolean zeroAbsence(IDisciplineAbsence disciplineAbsence) {
        return disciplineAbsence.getTotalAbsence() == null || disciplineAbsence.getTotalAbsence() == 0L;
    }
}
