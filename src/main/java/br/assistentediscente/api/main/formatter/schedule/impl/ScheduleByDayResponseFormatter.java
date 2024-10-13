package br.assistentediscente.api.main.formatter.schedule.impl;

import br.assistentediscente.api.integrator.enums.WeekDay;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineSchedule;
import br.assistentediscente.api.main.formatter.schedule.BaseScheduleFormatter;
import org.joda.time.DateTime;
import org.springframework.context.MessageSource;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static br.assistentediscente.api.main.formatter.schedule.ScheduleResponse.*;

public class ScheduleByDayResponseFormatter extends BaseScheduleFormatter {

    public ScheduleByDayResponseFormatter(MessageSource messageSource) {
        super(messageSource);
    }

    public String doResponse(String parameter, List<IDisciplineSchedule> disciplineList) {
        return checkDayAndFormatterToResponse(parameter, disciplineList);
    }

    private String checkDayAndFormatterToResponse(String dayShortName, List<IDisciplineSchedule> disciplines){

        if (disciplines == null || disciplines.isEmpty()){
            return getMessage(ERROR_DAY_RESPONSE);
        }

        return switch (dayIsTodayOrTomorrow(dayShortName)) {

            case 0 -> formatterDisciplineDayToSpeech(disciplines, dayShortName);

            case 1 -> //caso o dia perguntado for hoje
                    formatterDisciplineTodayToSpeech(disciplines, dayShortName);

            case 2 -> //caso o dia perguntado for amanhã
                    formatterDisciplineTomorrowToSpeech(disciplines, dayShortName);

            default -> getMessage(ERROR_DAY_RESPONSE);
        };

    }

    private String formatterDisciplineDayToSpeech(List<IDisciplineSchedule> disciplineList, String dayShortName) {

        WeekDay weekDay = WeekDay.getByShortName(dayShortName);

        if (Objects.nonNull(weekDay)){

            if(weekDay.equals(WeekDay.SATURDAY)){
                return makeSpeech(disciplineList, getMessage(START_GENERIC_RESPONSE_SATURDAY), dayShortName);
            }

            else {
                return makeSpeech(disciplineList, getMessage(START_GENERIC_RESPONSE_WEEK, weekDay.getFullName()), dayShortName);
            }
        }

        return "Nenhuma aula foi encontrada no dia solicitado";
    }

    private String formatterDisciplineTodayToSpeech(List<IDisciplineSchedule> disciplineList, String dayInFull){
        return makeSpeech(disciplineList, getMessage(START_RESPONSE_TODAY), dayInFull);
    }

    private String formatterDisciplineTomorrowToSpeech(List<IDisciplineSchedule> disciplineList, String dayInFull) {
        return makeSpeech(disciplineList, getMessage(START_RESPONSE_TOMORROW), dayInFull);
    }

    private  String makeSpeech(List<IDisciplineSchedule> disciplineList, String inicioResposta, String dayShortName) {
        StringBuilder speech =  new StringBuilder();
        speech.append(inicioResposta);

        for ( IDisciplineSchedule discipline : disciplineList){
            speech.append(discipline.getDisciplineName())
                    .append(" de ")
                    .append(arrangeHoraryToString(discipline, dayShortName))
                    .append(" com ")
                    .append(discipline.getTeacherName())
                    .append("e ");
        }

        String speechString = speech.toString();
        return speechString.substring(0, speechString.length()-2);
    }

    private String arrangeHoraryToString(IDisciplineSchedule discipline, String dayShortName) {

        String[] schedule = discipline.getDayStartEndHour().get(dayShortName).split("\\+");

        return getHoraryResult(schedule);
    }

    private int dayIsTodayOrTomorrow(String day){

        //defino que sera em portugues BR a escrita dos dias
        Locale locale = new Locale("pt","BR");

        //defino a timezone da jvm para a de Sao Paulo UTC -3
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));

        DateTime dateTime = new DateTime();

        //converto a data gerada para dia da semana abreviado em upperCase
        String dayOfWeek1 = new SimpleDateFormat("EE", locale).format(dateTime.toDate()).toUpperCase();
        String dayOfWeek = dayOfWeek1.substring(0, dayOfWeek1.length()-1);

        if (normalizeDayName(dayOfWeek).equals(day)){
            return 1;
        }

        dayOfWeek1 = new SimpleDateFormat("EE", locale).format(dateTime.plusDays(1).toDate()).toUpperCase();
        dayOfWeek = dayOfWeek1.substring(0, dayOfWeek1.length()-1);

        if (normalizeDayName(dayOfWeek).equals(day)){
            return 2;
        }

        return 0;
    }

    //Usado para tirar acento das palavras, nesse caso SÁB = SAB
    public static String normalizeDayName(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}
