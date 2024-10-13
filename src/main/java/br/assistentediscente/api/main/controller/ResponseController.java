package br.assistentediscente.api.main.controller;

import br.assistentediscente.api.main.dto.AutenticationResponse;
import br.assistentediscente.api.main.dto.InstitutionLoginFieldsDTO;
import br.assistentediscente.api.main.dto.LoginDTO;
import br.assistentediscente.api.main.dto.SkillResponse;
import br.assistentediscente.api.main.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api")
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping(value = "/make-response/{intent}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<SkillResponse> doResponseByIntent(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody List<String> params,
                                                             @PathVariable String intent){
        return ResponseEntity.ok(responseService.doResponseByIntent(intent, jwt.getSubject(), params));
    }

    @PostMapping(value = "/do-service/{serviceActivation}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<SkillResponse> doResponseByServiceActivation(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody Map<String,String> params,
                                                             @PathVariable String serviceActivation){
        return ResponseEntity.ok(responseService.callService(serviceActivation, jwt.getSubject(), params));
    }

    @CrossOrigin(origins = "https://auth-uegenio.app.guiliano.com.br")
    @PostMapping(value = "/authenticate-student", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "External Key of authenticated student",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error trying to authenticate student",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            }
    )
    private ResponseEntity<AutenticationResponse> autenticateStudent(@RequestBody LoginDTO loginDTO){
        return ResponseEntity.ok(responseService.authenticateStudent(
                loginDTO.username(), loginDTO.password(), loginDTO.institutionName()));
    }

    @CrossOrigin(origins = "https://auth-uegenio.app.guiliano.com.br")
    @GetMapping(path = "/login-fields/{institutionName}")
    private ResponseEntity<InstitutionLoginFieldsDTO> getInstitutionLoginFields(@PathVariable String institutionName){
        return ResponseEntity.ok(responseService.getInstitutionLoginFields(institutionName));
    }


}
