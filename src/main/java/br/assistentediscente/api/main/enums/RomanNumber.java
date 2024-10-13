package br.assistentediscente.api.main.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum RomanNumber {

    UM("I", "um"),
    DOIS("II", "dois"),
    TRES("III", "três"),
    QUATRO("IV", "quatro");

    private final String romanNumber;
    private final String wordNumber;
    RomanNumber(String romanNumber, String wordNumber){
        this.romanNumber = romanNumber;
        this.wordNumber = wordNumber;
    }

    public String getRomanNumber() {
        return romanNumber;
    }
    public String getWordNumber() {
        return wordNumber;
    }

    public static String getOrdinalByNumber(String romanNumberString){
        RomanNumber romanNumber =  Arrays.stream(values()).filter(value -> value.getRomanNumber().equals(romanNumberString)).findFirst().orElse(null);
        return Objects.requireNonNull(romanNumber).getWordNumber();
    }
    public static List<String> getAllRomanNumbersString() {
        return Arrays.stream(RomanNumber.values())
                .map(RomanNumber::getRomanNumber)
                .toList();
    }
}
