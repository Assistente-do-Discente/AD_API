package br.assistentediscente.api.institutionplugin.ueg.infos;

import br.assistentediscente.api.integrator.institutions.info.IDisciplineAbsence;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DisciplineAbsenceUEG implements IDisciplineAbsence {

    @SerializedName("disc_cursada")
    private String disciplineName;

    @SerializedName("gra_periodo")
    private String semesterActive;

    @SerializedName("mat_nfaltas")
    private Long totalAbsence;

    @SerializedName("mat_nfaltas_abonadas")
    private Long totalExcusedAbsences;

}
