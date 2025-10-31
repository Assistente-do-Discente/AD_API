package br.assistentediscente.api.institutionplugin.ueg.infos;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionActivitiesUEG {

    private Long aceRequired;
    private Long cceRequired;
    private Long aceCompleted;
    private Long cceCompleted;

    private Long totalHoursRequired;
    private Long totalHoursCompleted;

    @SerializedName("Extensao")
    private List<ExtensionDetailUEG> details;

}
