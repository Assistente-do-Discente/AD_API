package br.assistentediscente.api.integrator.exceptions.intent;

import br.assistentediscente.api.integrator.exceptions.BusinessException;

import static br.assistentediscente.api.integrator.enums.BusinessErrorMessage.ERROR_SCHEDULE_EMPTY_OR_NOT_FOUND;

public class ScheduleWeekEmptyOrNotFoundException extends BusinessException {

    public ScheduleWeekEmptyOrNotFoundException() {
        super(ERROR_SCHEDULE_EMPTY_OR_NOT_FOUND);
    }

    public ScheduleWeekEmptyOrNotFoundException(Object... parameters){
        super(ERROR_SCHEDULE_EMPTY_OR_NOT_FOUND, parameters);
    }

    public ScheduleWeekEmptyOrNotFoundException(String message) {
        super(message, ERROR_SCHEDULE_EMPTY_OR_NOT_FOUND.getCode());
    }
}