package br.assistentediscente.api.institutionplugin.ueg.infos;

import br.assistentediscente.api.integrator.institutions.info.IAcademicData;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AcademicDataUEG implements IAcademicData {

    @SerializedName("media_geral")
    private Float overallAverage;

    @SerializedName("tipo_contrato")
    private String studentSituation;

    @SerializedName("percent_cht_cumprida")
    private Float percentComplete;

    @SerializedName("nome_curso")
    private String courseName;
}
