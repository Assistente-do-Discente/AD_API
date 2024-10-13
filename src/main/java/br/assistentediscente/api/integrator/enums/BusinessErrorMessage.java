package br.assistentediscente.api.integrator.enums;

public enum BusinessErrorMessage {

    /*
        ERR 001 - 050 students errors
        ERR 051 - 100 institutions errors
        ERR 101 - 150 intents errors
        ERR 151 - 200 ai errors
        ERR 201 - 250 param errors
        ERR 251 - 300 service plugin errors
        ERR 301 - 350 file errors
     */

    ERROR_SESSION_EXPIRES("ERR-001", 404),
    ERROR_STUDENT_NOT_AUTHENTICATED("ERR-002", 400),

    ERROR_INSTITUTION_NOT_FOUND("ERR-051", 404),
    ERROR_INSTITUTION_PLUGIN_NOT_FOUND("ERR-052", 404),
    ERROR_COMUNICATION_WITH_INSTITUTION("ERR-053", 500),

    ERROR_INTENT_NOT_SUPPORTED("ERR-101", 500),
    ERROR_SCHEDULE_EMPTY_OR_NOT_FOUND("ERR-102", 404),
    ERROR_DISCIPLINE_NAME_NOT_FOUND("ERR-103", 404),

    ERROR_NO_AI_ACTIVE("ERR-151", 500),
    ERROR_AI_PLUGIN_NOT_FOUND("ERR-152", 500),

    ERROR_PARAMETER_NOT_FOUND("ERR-201", 400),

    ERROR_SERVICE_PLUGIN_NOT_FOUND("ERR-251", 400),
    ERROR_MANDATORY_PARAMETER_NOT_FILLED("ERR-252", 400),
    ERROR_PARAMETER_TYPE_NOT_SUPPORTED("ERR-253", 400),
    ERROR_SERVICE_PARAMETER_NOT_FILLED("ERR-254", 400),
    ERROR_SERVICE_PARAMETER_NOT_FOUND("ERR-255", 400),
    ERROR_COULD_NOT_EXECUTE_SERVICE("ERR-256", 400),

    ERROR_COULDNT_CREATE_FILE("ERR-301", 500),
    ERROR_FILE_NOT_FOUND("ERR-302", 500),
    ERROR_COULDNT_DELETE_FILE("ERR-303", 500),

    GENERIC_EXCEPTION_FOR_INSTITUTION("ERR-999", 500)
    ;

    private final String message;
    private final int code;

    BusinessErrorMessage(final String message, final Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
