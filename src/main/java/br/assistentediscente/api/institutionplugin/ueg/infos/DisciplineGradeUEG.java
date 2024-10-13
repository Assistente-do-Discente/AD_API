package br.assistentediscente.api.institutionplugin.ueg.infos;

import br.assistentediscente.api.integrator.institutions.info.IDetailedDisciplineGrade;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineGrade;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class DisciplineGradeUEG implements IDisciplineGrade {

    @SerializedName("disc_cursada")
    private String disciplineName;

    @SerializedName("mat_mediafinal")
    private Float finalMedia;

    @SerializedName("periodo_grade")
    private String semester;

    @ToString.Exclude
    private List<IDetailedDisciplineGrade> detailedGrades;

}
