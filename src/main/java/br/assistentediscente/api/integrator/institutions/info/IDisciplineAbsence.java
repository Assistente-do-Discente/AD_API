package br.assistentediscente.api.integrator.institutions.info;

public interface IDisciplineAbsence extends IDiscipline{

    String getSemesterActive();
    Long getTotalAbsence();
    Long getTotalExcusedAbsences();
}
