package br.assistentediscente.api.institutionplugin.ueg.infos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ComplementaryActivitiesUEG {

    private Long hourRequired;
    private Long hourCompleted;

    @SerializedName("lista")
    private List<ComplementaryDetailUEG> details;
}
