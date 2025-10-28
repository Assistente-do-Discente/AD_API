package br.assistentediscente.api.institutionplugin.ueg.converter;

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
public class ResponseTool implements IResponseTool {
    private String response;
    private Object data;
}
