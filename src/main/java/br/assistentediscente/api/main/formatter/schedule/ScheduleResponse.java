package br.assistentediscente.api.main.formatter.schedule;

import br.assistentediscente.api.main.formatter.IFormatterResponse;

public enum ScheduleResponse implements IFormatterResponse {

    START_RESPONSE_TODAY("SCHEDULE-MSG-001"),
    START_RESPONSE_TOMORROW("SCHEDULE-MSG-002"),
    START_GENERIC_RESPONSE_WEEK("SCHEDULE-MSG-003"),
    START_GENERIC_RESPONSE_SATURDAY("SCHEDULE-MSG-004"),
    START_RESPONSE_DISCIPLINE_NAME("SCHEDULE-MSG-005"),

    ERROR_DAY_RESPONSE("SCHEDULE-ERR-001"),
    ERROR_DISCIPLINE_NAME_RESPONSE("SCHEDULE-ERR-002"),

    ;
    private final String message;

    ScheduleResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
