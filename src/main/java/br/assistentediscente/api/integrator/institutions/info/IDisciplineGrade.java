package br.assistentediscente.api.integrator.institutions.info;

import java.util.List;

public interface IDisciplineGrade extends IDiscipline{

    Float getFinalMedia();
    String getSemester();
    List<IDetailedDisciplineGrade> getDetailedGrades();
}
