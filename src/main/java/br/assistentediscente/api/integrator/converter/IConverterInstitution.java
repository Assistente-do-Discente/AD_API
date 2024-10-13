package br.assistentediscente.api.integrator.converter;

import br.assistentediscente.api.integrator.institutions.info.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

public interface IConverterInstitution {

    IDisciplineSchedule getDisciplineFromJson(JsonElement jsonElement);
    List<IDisciplineSchedule> getDisciplinesWithScheduleFromJson(JsonArray jsonArray);
    List<IDisciplineGrade> getGradesWithDetailedGradeFromJson(JsonArray jsonArray);
    List<IDisciplineGrade> getGradesBySemesterFromJson(JsonArray jsonElements, String semester);
    IAcademicData getAcademicDataFromJson(JsonElement jsonElement);
    List<IDisciplineAbsence> getDisciplinesWithAbsencesFromJson(JsonArray jsonElements);
    IStudentData getStudentDataFromJson(JsonElement jsonElement);
}
