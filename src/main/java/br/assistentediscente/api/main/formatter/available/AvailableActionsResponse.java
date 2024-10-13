package br.assistentediscente.api.main.formatter.available;

import br.assistentediscente.api.main.formatter.IFormatterResponse;

public enum AvailableActionsResponse implements IFormatterResponse {

    AVAILABLE_ACTIONS_RESPONSE("AVAILABLE-MSG-001");

    private final String message;

    AvailableActionsResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
