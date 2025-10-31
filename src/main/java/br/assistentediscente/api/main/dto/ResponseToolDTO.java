package br.assistentediscente.api.main.dto;

import br.assistentediscente.api.integrator.converter.IResponseTool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseToolDTO implements IResponseTool {
    private String response;
    private Object data;
}
