package br.assistentediscente.api.main.formatter.schedule.impl;

import br.assistentediscente.api.integrator.enums.WeekDay;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineSchedule;
import br.assistentediscente.api.main.formatter.schedule.BaseScheduleFormatter;
import org.springframework.context.MessageSource;

import java.util.List;

import static br.assistentediscente.api.main.formatter.schedule.ScheduleResponse.ERROR_DISCIPLINE_NAME_RESPONSE;
import static br.assistentediscente.api.main.formatter.schedule.ScheduleResponse.START_RESPONSE_DISCIPLINE_NAME;

public class ScheduleByDisciplineNameFormatter extends BaseScheduleFormatter {

    public ScheduleByDisciplineNameFormatter(MessageSource messageSource) {
        super(messageSource);
    }

    public String doResponse(String disciplineName, List<IDisciplineSchedule> disciplineList) {

        if (disciplineList == null || disciplineList.isEmpty()) {
            return getMessage(ERROR_DISCIPLINE_NAME_RESPONSE, disciplineName);
        }

        StringBuilder response = new StringBuilder();
        IDisciplineSchedule discipline = disciplineList.get(0);
        response.append(getMessage(START_RESPONSE_DISCIPLINE_NAME));

        disciplineName = switchRomanNumberToNumber(disciplineName);

        response.append(disciplineName);

        buildResponse(discipline, response);

        response.replace(response.length() - 2, response.length(), "");
        return response.toString().trim();
    }

    private static void buildResponse(IDisciplineSchedule discipline, StringBuilder response) {
        for(int j = 0; j < discipline.getDayStartEndHour().size() ; j++) {
            WeekDay day = WeekDay.getByShortName(discipline.getScheduleList().get(j).getDay());
            response.append(day != WeekDay.SATURDAY ? " às " : " aos ");
            response.append(day.getFullName())
                    .append("s de ");
            String[] schedule = discipline.getDayStartEndHour()
                    .get(discipline.getScheduleList().get(j).getDay()).split("\\+");
            response.append(getHoraryResult(schedule))
                    .append(" com ")
                    .append(discipline.getTeacherName())
                    .append(", ");
        }
    }

    private String arrangeHourToResponse(String hour){
        StringBuilder hourArranged = new StringBuilder();
        String[] hourSplit = hour.split(":");
        hourArranged.append(hourSplit[0]);

        if (Integer.parseInt(hourSplit[1]) > 0){
            hourArranged.append(" e ");
            hourArranged.append(hourSplit[1]);
        }
        return hourArranged.toString();
    }

    private String getDayFullName(String dayShortName) {
        return WeekDay.getByShortName(dayShortName).getFullName();
    }
}
