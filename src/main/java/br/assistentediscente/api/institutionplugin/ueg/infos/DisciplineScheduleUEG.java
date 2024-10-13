package br.assistentediscente.api.institutionplugin.ueg.infos;

import br.assistentediscente.api.integrator.institutions.info.IDisciplineSchedule;
import br.assistentediscente.api.integrator.institutions.info.ISchedule;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Getter
@Setter
public class DisciplineScheduleUEG implements IDisciplineSchedule {

    @SerializedName("disciplina")
    private String disciplineName;

    @ToString.Exclude
    private List<ISchedule> scheduleList;

    private String teacherName;

    private Map<String, String> dayStartEndHour = new HashMap<>();
}
