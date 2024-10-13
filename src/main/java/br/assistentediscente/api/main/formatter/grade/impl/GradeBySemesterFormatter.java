package br.assistentediscente.api.main.formatter.grade.impl;

import br.assistentediscente.api.integrator.institutions.info.IDisciplineGrade;
import br.assistentediscente.api.main.formatter.grade.BaseGradeFormatter;
import org.springframework.context.MessageSource;

import java.util.List;

import static br.assistentediscente.api.main.formatter.grade.GradeResponse.ERROR_SEMESTER_GRADES_NOT_FOUND;
import static br.assistentediscente.api.main.formatter.grade.GradeResponse.START_SEMESTER_GRADES_RESPONSE;

public class GradeBySemesterFormatter extends BaseGradeFormatter {

    public GradeBySemesterFormatter(MessageSource messageSource) {
        super(messageSource);
    }

    public String doResponse(String semester, List<IDisciplineGrade> semesterGrades) {

        if (checkGrades(semesterGrades))
            return getMessage(ERROR_SEMESTER_GRADES_NOT_FOUND, semester);

        StringBuilder response = new StringBuilder();
        response.append(getMessage(START_SEMESTER_GRADES_RESPONSE, semester));
        buildResponse(semesterGrades, response);
        return response.toString();
    }

    private void buildResponse(List<IDisciplineGrade> semesterGrades, StringBuilder response) {

        for (IDisciplineGrade grade : semesterGrades) {
            String disciplineName = switchRomanNumberToNumber(grade.getDisciplineName());
            response.append(disciplineName)
                    .append(": ")
                    .append(grade.getFinalMedia())
                    .append(". ");
        }

        response.deleteCharAt(response.length() - 1);
    }

    private boolean checkGrades(List<IDisciplineGrade> semesterGrades) {
        return semesterGrades == null || semesterGrades.isEmpty() ||
                semesterGrades.stream().allMatch(grade -> grade.getFinalMedia() == 0);
    }

}
