package br.assistentediscente.api.institutionplugin.ueg.formatter;

import br.assistentediscente.api.institutionplugin.ueg.infos.ComplementaryActivitiesUEG;
import br.assistentediscente.api.institutionplugin.ueg.infos.ExtensionActivitiesUEG;
import br.assistentediscente.api.integrator.enums.WeekDay;
import br.assistentediscente.api.integrator.institutions.info.*;

import java.util.List;

public class FormatterMessageResponse {

    public String getSchedule(List<IDisciplineSchedule> list) {
        return "";
    }

    public String getScheduleByWeekDay(List<IDisciplineSchedule> list, WeekDay weekDay) {
        StringBuilder message = new StringBuilder();
        message.append("Horários de aula de ").append(weekDay.getFullName()).append("*\n\n");

        boolean foundAny = false;
        for (IDisciplineSchedule discipline : list) {
            List<ISchedule> schedules = discipline.getScheduleList();

            if (!schedules.isEmpty()) {
                foundAny = true;
                message.append("*").append(discipline.getDisciplineName()).append("*\n");
                message.append("Professor: ").append(discipline.getTeacherName()).append("\n");

                for (ISchedule s : schedules) {
                    message.append(s.getStartTime())
                            .append(" às ").append(s.getEndTime())
                            .append(" — ").append("Sala ").append(s.getClassroom())
                            .append("\n");
                }
                message.append("\n");
            }
        }

        if (!foundAny) {
            message.append("Nenhuma aula encontrada para este dia.");
        }

        return message.toString().trim();
    }

    public String getScheduleByDisciplineName(List<IDisciplineSchedule> list, String disciplineName) {
        StringBuilder message = new StringBuilder();

        boolean foundAny = false;
        for (IDisciplineSchedule discipline : list) {
            message.append("Horários de aula da disciplina ").append(disciplineName).append("*\n\n");
            List<ISchedule> schedules = discipline.getScheduleList();

            if (!schedules.isEmpty()) {
                foundAny = true;
                message.append("*").append(discipline.getDisciplineName()).append("*\n");
                message.append("Professor: ").append(discipline.getTeacherName()).append("\n");

                for (ISchedule s : schedules) {
                    message.append(s.getStartTime())
                            .append(" às ").append(s.getEndTime())
                            .append(" — ").append("Sala ").append(s.getClassroom())
                            .append("\n");
                }
                message.append("\n");
            }
        }

        if (!foundAny) {
            message.append("Nenhum horário encontrado para esta disciplina.");
        }

        return message.toString().trim();
    }

    public String getGrades(List<IDisciplineGrade> list) {
        return "";
    }

    public String getGradesByDisciplineName(List<IDisciplineGrade> list, String disciplineName) {
        StringBuilder message = new StringBuilder();
        message.append("*Notas de ").append(disciplineName).append("*\n\n");

        boolean found = false;

        for (IDisciplineGrade grade : list) {
            if (grade.getDisciplineName().equalsIgnoreCase(disciplineName)) {
                found = true;

                message.append("*Semestre:* ").append(grade.getSemester()).append("\n\n");

                if (grade.getDetailedGrades() != null && !grade.getDetailedGrades().isEmpty()) {
                    message.append("*Detalhamento das Avaliações:*\n");
                    for (IDetailedDisciplineGrade detail : grade.getDetailedGrades()) {
                        message.append("• ").append(detail.getBimester()).append(": ")
                                .append(detail.getGradeValue())
                                .append(" (peso ").append(detail.getGradeWeight()).append(")\n");
                    }
                    message.append("\n");
                }

                Float finalMedia = grade.getFinalMedia();
                message.append("🧮 *Média Final:* ")
                        .append(finalMedia != null ? finalMedia : "N/A")
                        .append("\n");

                break;
            }
        }

        if (!found) {
            message.append("Nenhuma nota encontrada para a disciplina informada.");
        }

        return message.toString().trim();
    }

    public String getGradesBySemester(List<IDisciplineGrade> list, String semester) {
        StringBuilder message = new StringBuilder();
        message.append("*Notas do semestre ").append(semester).append("*\n\n");

        boolean found = false;

        for (IDisciplineGrade grade : list) {
            found = true;

            message.append("*Disciplina:* ").append(grade.getDisciplineName()).append("\n\n");

            if (grade.getDetailedGrades() != null && !grade.getDetailedGrades().isEmpty()) {
                message.append("*Detalhamento das Avaliações:*\n");
                for (IDetailedDisciplineGrade detail : grade.getDetailedGrades()) {
                    message.append("• ").append(detail.getBimester()).append(": ")
                            .append(detail.getGradeValue())
                            .append(" (peso ").append(detail.getGradeWeight()).append(")\n");
                }
                message.append("\n");
            }

            Float finalMedia = grade.getFinalMedia();
            message.append("*Média Final:* ")
                    .append(finalMedia != null ? finalMedia : "N/A")
                    .append("\n");

        }

        if (!found) {
            message.append("Nenhuma nota encontrada.");
        }

        return message.toString().trim();
    }

    public String getAcademicData(IAcademicData data) {
        if (data == null || data.getOverallAverage() == null) {
            return "Não foi possível obter a média geral do aluno.";
        } else {
            return "Sua média geral é: " + data.getOverallAverage() + ".";
        }

    }

    public String getActiveDisciplinesWithAbsencesByDisciplineName(List<IDisciplineAbsence> list, String disciplineName) {
        StringBuilder message = new StringBuilder();
        message.append("📘 *Faltas em ").append(disciplineName).append("*\n\n");

        boolean found = false;

        for (IDisciplineAbsence discipline : list) {
            if (discipline.getDisciplineName().equalsIgnoreCase(disciplineName)) {
                found = true;

                message.append("*Semestre:* ").append(discipline.getSemesterActive()).append("\n\n");

                Long total = discipline.getTotalAbsence() != null ? discipline.getTotalAbsence() : 0L;
                Long justified = discipline.getTotalExcusedAbsences() != null ? discipline.getTotalExcusedAbsences() : 0L;

                message.append("*Total de faltas:* ").append(total).append("\n").append("*Faltas abonadas:* ").append(justified).append("\n");

                break;
            }
        }

        if (!found) {
            message.append("Nenhuma informação de faltas encontrada para esta disciplina.");
        }

        return message.toString().trim();
    }

    public String getActiveDisciplinesWithAbsencesBySemester(List<IDisciplineAbsence> list, String semester) {
        StringBuilder message = new StringBuilder();
        message.append("📘 *Faltas do semestre ").append(semester).append("*\n\n");

        boolean found = false;

        for (IDisciplineAbsence discipline : list) {
            if (discipline.getSemesterActive().equalsIgnoreCase(semester)) {
                found = true;

                message.append("*Disciplina:* ").append(discipline.getDisciplineName()).append("\n");

                Long total = discipline.getTotalAbsence() != null ? discipline.getTotalAbsence() : 0L;
                Long justified = discipline.getTotalExcusedAbsences() != null ? discipline.getTotalExcusedAbsences() : 0L;

                message.append("*Total de faltas:* ").append(total).append("\n").append("*Faltas abonadas:* ").append(justified).append("\n\n");
            }
        }

        if (!found) {
            message.append("Nenhuma informação de faltas encontrada para este semestre.");
        }

        return message.toString().trim();
    }

    public String getExtensionHours(ExtensionActivitiesUEG data) {
        return "Você tem um total de "+data.getTotalHoursCompleted()+" horas de atividades de extensão de "+data.getTotalHoursRequired()+" horas exigidas";
    }

    public String getComplementaryHours(ComplementaryActivitiesUEG data) {
        return "Você tem um total de "+data.getHourCompleted()+" horas de atividades complementares de "+data.getHourRequired()+" horas exigidas";
    }

    public String getStudentData(IStudentData studentData) {
        return "";
    }

    public String getActiveDisciplinesWithAbsences(List<IDisciplineAbsence> list) {
        return "";
    }
}
