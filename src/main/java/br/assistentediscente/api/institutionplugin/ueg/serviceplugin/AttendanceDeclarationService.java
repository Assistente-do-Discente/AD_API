package br.assistentediscente.api.institutionplugin.ueg.serviceplugin;

import br.assistentediscente.api.institutionplugin.ueg.UEGPlugin;
import br.assistentediscente.api.institutionplugin.ueg.infos.StudentDataUEG;
import br.assistentediscente.api.institutionplugin.ueg.serviceplugin.parameter.StudentParameter;
import br.assistentediscente.api.integrator.exceptions.GenericBusinessException;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;
import br.assistentediscente.api.integrator.plataformeservice.EmailDetails;
import br.assistentediscente.api.integrator.plataformeservice.IPlataformService;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;
import br.assistentediscente.api.integrator.serviceplugin.parameters.ParameterValue;
import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static br.assistentediscente.api.institutionplugin.ueg.enums.DocEnum.ATTENDANCE_DECLARATION;

public class AttendanceDeclarationService implements IServicePlugin {

    public List<String> getActivationName() {
        return List.of("gerar declaração de frequência",
                "declaracao de frequencia",
                "declaração de frequencia",
                "declaração frequencia", "declaracao frequencia");
    }

    public List<AParameter> getParameters() {
        return List.of(new StudentParameter());
    }

    public Set<ParameterValue> getParameterValues(IBaseInstitutionPlugin institution, Map<String, String> parameters) {
        StudentParameter studentParameter = new StudentParameter();
        ParameterValue parameterValue = new ParameterValue(studentParameter,
                studentParameter.getValueFromInstitution(institution));
        return Set.of(parameterValue);
    }

    public String doService(IBaseInstitutionPlugin institution, Set<ParameterValue> parameterValues,
                            IPlataformService plataformService) {

        StudentDataUEG studentData = getStudentData(parameterValues);

        validateStudentEmail(studentData);

        try{
            String pdfPath = generateAttendanceDeclarationPDF(institution, plataformService);

            EmailDetails emailDetails = buildEmailDetails(studentData, pdfPath);

            return sendEmail(plataformService, emailDetails);
        } catch (RuntimeException e) {
            throw new GenericBusinessException("Houve um erro ao enviar sua declaração de frequência, tente novamente mais tarde");
        }

    }

    private StudentDataUEG getStudentData(Set<ParameterValue> parameterValues) {
        return parameterValues.stream()
                .findFirst()
                .map(parameterValue -> (StudentDataUEG) parameterValue.getValue())
                .orElseThrow(() -> new GenericBusinessException
                        ("Não foram encontrados os dados do estudante para envio da declaração"));
    }

    private void validateStudentEmail(StudentDataUEG studentData) {
        if (Objects.isNull(studentData) ||
                Objects.isNull(studentData.getEmail()) || studentData.getEmail().isEmpty()) {
            throw new GenericBusinessException("Não foi encontrado o email do estudante para envio da declaração");
        }
    }

    private String generateAttendanceDeclarationPDF(IBaseInstitutionPlugin institution, IPlataformService plataformService) {
        String attendanceDeclarationHTML = ((UEGPlugin) institution).generateNewAttendanceDeclarationHTML();
        return plataformService.HTMLToPDF(attendanceDeclarationHTML, ATTENDANCE_DECLARATION.getFolderPath(),
                ATTENDANCE_DECLARATION.getFilePrefix());

    }

    private EmailDetails buildEmailDetails(StudentDataUEG studentData, String pdfPath) {
        return new EmailDetails(studentData.getFirstName(), studentData.getEmail(),
                "DECLARAÇÃO DE FREQUENCIA UEG",
                "Olá, segue em anexo sua declaração de frequencia como estudante da UEG",
                "Declaracao_Frequencia", pdfPath);
    }

    private String sendEmail(IPlataformService plataformService, EmailDetails emailDetails) {
        if (plataformService.sendEmailWithFileAttachment(emailDetails)) {
            return "Sua declaração de frequência foi enviada para o seu e-mail acadêmico.";
        }
        return "Houve um erro ao enviar sua declaração de frequência, tente novamente mais tarde";
    }

}
