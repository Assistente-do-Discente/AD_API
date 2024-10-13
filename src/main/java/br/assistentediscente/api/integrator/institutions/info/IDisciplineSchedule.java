package br.assistentediscente.api.integrator.institutions.info;

import java.util.List;
import java.util.Map;

public interface IDisciplineSchedule extends IDiscipline{

    String getTeacherName();
    List<ISchedule> getScheduleList();
    Map<String, String> getDayStartEndHour(); // 19:00:00;19:50:00+19:50:00;20:40:00 exemplo de como deve ser o value
}
