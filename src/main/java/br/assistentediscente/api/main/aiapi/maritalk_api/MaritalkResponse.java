package br.assistentediscente.api.main.aiapi.maritalk_api;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class MaritalkResponse {

    @SerializedName("answer")
    private String response;
}
