package br.assistentediscente.api.integrator.institutions.info;

public interface ISchedule {

    String getDay();
    String getStartTime();
    String getEndTime();
    String getTeacherName();
    String getClassroom();
}
