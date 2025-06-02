package br.assistentediscente.api.integrator.institutions;

import br.assistentediscente.api.integrator.converter.IBaseTool;
import br.assistentediscente.api.integrator.enums.WeekDay;
import br.assistentediscente.api.integrator.exceptions.institution.InstitutionComunicationException;
import br.assistentediscente.api.integrator.exceptions.intent.IntentNotSupportedException;
import br.assistentediscente.api.integrator.exceptions.student.StudentNotAuthenticatedException;
import br.assistentediscente.api.integrator.institutions.info.*;
import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IBaseInstitutionPlugin {

    List<KeyValue> authenticateStudent(String username, String password) throws StudentNotAuthenticatedException, InstitutionComunicationException;
    void setStudentAccessData(List<KeyValue> accessData);
    List<KeyValue> refreshStudentAccessData(List<KeyValue> accessData);
    List<IDisciplineSchedule> getScheduleByWeekDay(WeekDay weekDay) throws IntentNotSupportedException;
    //Não vai ser implementado na UEG, apenas tratamento de exception
    Integer getAbsenceByDisciplineAndDate(String disciplineName, LocalDate date) throws IntentNotSupportedException;
    List<IDisciplineSchedule> getWeekSchedule() throws IntentNotSupportedException;
    List<IDisciplineSchedule> getScheduleByDisciplineName(String disciplineToGetSchedule) throws IntentNotSupportedException;
    List<IDisciplineGrade> getGrades() throws IntentNotSupportedException, InstitutionComunicationException;
    List<IDisciplineGrade> getGradesBySemester(String semester) throws IntentNotSupportedException, InstitutionComunicationException;
    IAcademicData getAcademicData() throws IntentNotSupportedException, InstitutionComunicationException;
    List<IDisciplineAbsence> getActiveDisciplinesWithAbsences() throws IntentNotSupportedException, InstitutionComunicationException;
    IStudentData getStudentData() throws IntentNotSupportedException, InstitutionComunicationException;
    Set<Class<? extends IServicePlugin>> getAllServicePlugins();
    List<IBaseTool> getAllInformationToolsPlugins();

}
