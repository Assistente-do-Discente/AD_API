package br.assistentediscente.api.integrator.enums;

import lombok.Getter;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

@Getter
public enum WeekDay {

    MONDAY("SEG", "Segunda-feira"),
    TUESDAY("TER", "Terça-feira"),
    WEDNESDAY("QUA", "Quarta-feira"),
    THURSDAY("QUI", "Quinta-feira"),
    FRIDAY("SEX", "Sexta-feira"),
    SATURDAY("SAB", "Sábado"),
    SUNDAY("DOM", "Domingo");

    private final String shortName;
    private final String fullName;

    WeekDay(final String shortName, final String fullName){
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public static WeekDay getByShortName(final String shortName) {
        return Arrays.stream(values()).filter(value ->
                value.getShortName().equals(shortName))
                .findFirst().orElse(null);
    }

    public static WeekDay getByDate(String date){

        Locale locale = new Locale("pt","BR");
        LocalDate localDate = LocalDate.parse(date);
        String weekDay = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, locale).toUpperCase();

        return WeekDay.getByShortName(normalizeDayName(weekDay.substring(0, weekDay.length()-1)));
    }

    //Usado para tirar acento das palavras, nesse caso SÁB = SAB
    public static String normalizeDayName(String dayNameToNormalize) {
        String nfdNormalizedString = Normalizer.normalize(dayNameToNormalize, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}
