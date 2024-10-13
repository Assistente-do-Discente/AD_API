package br.assistentediscente.api.main.formatter.grade;

import br.assistentediscente.api.main.formatter.IFormatterResponse;

public enum GradeResponse implements IFormatterResponse {

    START_DISCIPLINE_GRADE_RESPONSE("GRADE-MSG-001"),
    BIMESTER_RESPONSE("GRADE-MSG-002"),
    FINAL_MEDIA_RESPONSE("GRADE-MSG-003"),
    ERROR_DISCIPLINE_GRADE_NOT_FOUND_RESPONSE("GRADE-ERR-001"),


    ERROR_SEMESTER_GRADES_NOT_FOUND("GRADE-ERR-002"),
    START_SEMESTER_GRADES_RESPONSE("GRADE-MSG-004");
    private final String message;

    GradeResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
