package br.assistentediscente.api.main.formatter.grade.impl;

import br.assistentediscente.api.integrator.institutions.info.IDetailedDisciplineGrade;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineGrade;
import br.assistentediscente.api.main.formatter.grade.BaseGradeFormatter;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Objects;

import static br.assistentediscente.api.main.formatter.grade.GradeResponse.*;

public class GradeByDisciplineNameFormatter extends BaseGradeFormatter {


    public GradeByDisciplineNameFormatter(MessageSource messageSource) {
        super(messageSource);
    }

    public String doResponse(String disciplineName, List<IDisciplineGrade> gradeList) {
        
        IDisciplineGrade grade = getDisciplineGrade(disciplineName, gradeList);

        if (Objects.isNull(grade)) {
            return getMessage(ERROR_DISCIPLINE_GRADE_NOT_FOUND_RESPONSE, new Object[]{disciplineName});
        }

        StringBuilder response = new StringBuilder();

        buildResponse(disciplineName, response, grade);

        return response.toString();
    }

    private void buildResponse(String disciplineName, StringBuilder response, IDisciplineGrade grade) {

        disciplineName = switchRomanNumberToNumber(disciplineName);

        response.append(getMessage(START_DISCIPLINE_GRADE_RESPONSE, disciplineName))
                .append(" ");

        int i = 0;
        for (IDetailedDisciplineGrade detailedGrade : grade.getDetailedGrades()){
            i++;
            if (i>1) response.append(", ");
            response.append(getMessage(BIMESTER_RESPONSE,
                    i,detailedGrade.getGradeValue()));
        }
        response.deleteCharAt(response.length()-1);
        response.append(". ")
                .append(getMessage(FINAL_MEDIA_RESPONSE,
                grade.getFinalMedia()));
    }

    private IDisciplineGrade getDisciplineGrade(String disciplineName, List<IDisciplineGrade> gradeList) {
        for (IDisciplineGrade grade : gradeList ){
            if (grade.getDisciplineName().trim().equalsIgnoreCase(disciplineName.trim())){
                return grade;
            }
        }
        return null;
    }


}
