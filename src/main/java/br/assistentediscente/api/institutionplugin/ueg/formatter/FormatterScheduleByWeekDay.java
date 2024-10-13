package br.assistentediscente.api.institutionplugin.ueg.formatter;

import br.assistentediscente.api.integrator.enums.WeekDay;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FormatterScheduleByWeekDay {

    public List<IDisciplineSchedule> disciplinesWithScheduleByDay(WeekDay day, List<IDisciplineSchedule> disciplines){
        if(disciplines == null || !disciplines.isEmpty()){
            return sortDisciplinesByDayAndStartHourClass(day.getShortName(), doWeeklyClassSchedule(disciplines));
        }
        return null;
    }

    private HashMap<String, List<IDisciplineSchedule>> doWeeklyClassSchedule(List<IDisciplineSchedule> disciplineList) {

        if (Objects.isNull(disciplineList) || disciplineList.isEmpty()){
            return null;
        }
        HashMap<String, List<IDisciplineSchedule>> weeklyClassSchedule = new HashMap<>();

        for (IDisciplineSchedule discipline : disciplineList) {
            discipline.getDayStartEndHour().forEach((day, startEndHour) -> {
                if (!weeklyClassSchedule.containsKey(day)) {
                    List<IDisciplineSchedule> disciplines = new ArrayList<>();
                    disciplines.add(discipline);
                    weeklyClassSchedule.put(day, disciplines);
                } else {
                    weeklyClassSchedule.get(day).add(discipline);
                }
            });
        }

        return weeklyClassSchedule;
    }

    private static List<IDisciplineSchedule> sortDisciplinesByDayAndStartHourClass(String dayShortName, Map<String, List<IDisciplineSchedule>> disciplineList) {

        if (Objects.isNull(disciplineList) || disciplineList.isEmpty())
            return new ArrayList<IDisciplineSchedule>();

        List<IDisciplineSchedule> disciplines = disciplineList.get(dayShortName);

        if (Objects.isNull(disciplines) || disciplines.isEmpty())
            return new ArrayList<IDisciplineSchedule>();

        disciplines.sort((discipline1, discipline2) -> {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            int result;
            try {
                result = sdf.parse(discipline1.getDayStartEndHour().get(dayShortName).split(";")[0])
                        .compareTo(sdf.parse
                                (discipline2.getDayStartEndHour().get(dayShortName).split(";")[0]));
            } catch (ParseException ex) {
                return 0;
            }

            return result;
        });
        return disciplines;
    }
}
