package br.assistentediscente.api.main.enums;

import java.util.Arrays;
import java.util.Objects;

public enum Ordinals {

    PRIMEIRO(1, "primeiro"),
    SEGUNDO(2, "segundo"),
    TERCEIRO(3, "terceiro"),
    QUARTO(4, "quarto"),
    QUINTO(5, "quinto"),
    SEXTO(6, "sexto"),
    SETIMO(7, "sétimo"),
    OITAVO(8, "oitavo"),
    NONO(9, "nono"),
    DECIMO(10, "décimo");


    private final int number;
    private final String ordinal;

    Ordinals(int number, String ordinal){
        this.ordinal = ordinal;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getOrdinal() {
        return ordinal;
    }

    public static String getOrdinalByNumber(int number){
        Ordinals ordinal =  Arrays.stream(values()).filter(value -> value.getNumber() == number).findFirst().orElse(null);
        return Objects.requireNonNull(ordinal).getOrdinal();
    }
}


