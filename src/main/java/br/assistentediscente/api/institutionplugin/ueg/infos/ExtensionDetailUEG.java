package br.assistentediscente.api.institutionplugin.ueg.infos;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionDetailUEG {

    @SerializedName("titulo")
    private String title;

    @SerializedName("nome")
    private String responsible;

    @SerializedName("acao_dt_inicial")
    private String initialDate;

    @SerializedName("acao_dt_final")
    private String finalDate;

    @SerializedName("carga_horaria")
    private String hours;

    @SerializedName("ch_qtde_cce")
    private String approvedHours;

}
