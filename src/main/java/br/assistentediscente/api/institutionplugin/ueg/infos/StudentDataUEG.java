package br.assistentediscente.api.institutionplugin.ueg.infos;

import br.assistentediscente.api.integrator.institutions.info.IStudentData;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDataUEG implements IStudentData {

    @SerializedName("acu_id")
    private String personId;

    @SerializedName("nome")
    private String firstName;

    @SerializedName("email_discente")
    private String email;
}
