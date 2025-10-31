package br.assistentediscente.api.institutionplugin.ueg.infos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ComplementaryDetailUEG {

    @SerializedName("horas_solicitadas")
    private String solicitedHours;

    @SerializedName("horas_aprovadas")
    private String approvedHours;

    @SerializedName("dt_cadastro")
    private String solicitedDate;

    @SerializedName("inicio_atividade")
    private String startDate;

    @SerializedName("fim_atividade")
    private String endDate;

    @SerializedName("fk_ac_rac_modalidade")
    private String modalityCode;

    @SerializedName("descricao")
    private String description;

    @SerializedName("instituicao")
    private String institution;

    @SerializedName(" local")
    private String local;

    @SerializedName("presencial")
    private String inPerson;

    @SerializedName(" fk_ac_tipo")
    private String typeCode;

    @SerializedName("modalidade")
    private String modality;

    @SerializedName("tipo")
    private String type;

    @SerializedName("homologacao_justificativa")
    private String homolJustify;

    @SerializedName("homologacao_aprovado")
    private String homolApproved;

    @SerializedName("inativo_motivo")
    private String inactiveMotive;

    @SerializedName("ch_exigida")
    private String hourLimit;

    @SerializedName("ch_cumprida")
    private String hourReached;

}
