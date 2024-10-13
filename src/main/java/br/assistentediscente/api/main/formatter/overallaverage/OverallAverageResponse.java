package br.assistentediscente.api.main.formatter.overallaverage;

import br.assistentediscente.api.main.formatter.IFormatterResponse;

public enum OverallAverageResponse implements IFormatterResponse {

    START_OVERALL_RESPONSE("OVERALL-MSG-001"),
    ERROR_OVERALL_RESPONSE("OVERALL-ERR-001")
    ;
    private final String message;

    OverallAverageResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
