package br.assistentediscente.api.main.formatter.absence;

import br.assistentediscente.api.main.formatter.IFormatterResponse;

public enum AbsenceResponse implements IFormatterResponse {

    RESPONSE_TOTAL_ABSENCE("ABSENCE-MSG-001"),
    RESPONSE_ZERO_ABSENCE("ABSENCE-MSG-002"),
    RESPONSE_TOTAL_ABSENCE_WITH_EXCUSED("ABSENCE-MSG-003"),

    ;
    private final String message;

    AbsenceResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}