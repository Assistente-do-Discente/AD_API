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

import static br.assistentediscente.api.institutionplugin.ueg.enums.DocEnum.ACADEMIC_RECORD;

public class AcademicRecordService implements IServicePlugin {

    public String getName() {
        return "generateAcademicRecord";
    }

    public String getDescription() {
        return "Serviço utilizado para gerar e enviar por email o histórico acadêmico do estudante.";
    }

    public List<String> getActivationName() {
        return List.of("gerar histórico acadêmico", "historico academico",
                "historico",
                "histórico acadêmico");
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
            String pdfPath = generateAcademicRecordPDF(institution, plataformService);

            EmailDetails emailDetails = buildEmailDetails(studentData, pdfPath);

            return sendEmail(plataformService, emailDetails);
        } catch (RuntimeException e) {
            throw new GenericBusinessException("Houve um erro ao enviar seu histórico acadêmico, tente novamente mais tarde");
        }

    }

    private StudentDataUEG getStudentData(Set<ParameterValue> parameterValues) {
        return parameterValues.stream()
                .findFirst()
                .map(parameterValue -> (StudentDataUEG) parameterValue.getValue())
                .orElseThrow(() -> new GenericBusinessException
                        ("Não foram encontrados os dados do estudante para envio do histórico acadêmico"));
    }

    private void validateStudentEmail(StudentDataUEG studentData) {
        if (Objects.isNull(studentData) ||
                Objects.isNull(studentData.getEmail()) || studentData.getEmail().isEmpty()) {
            throw new GenericBusinessException("Não foi encontrado o email do estudante para envio da histórico");
        }
    }

    private String generateAcademicRecordPDF(IBaseInstitutionPlugin institution, IPlataformService plataformService) {
        String attendanceDeclarationHTML = ((UEGPlugin) institution).generateNewAcademicRecordHTML();
        return plataformService.HTMLToPDF(attendanceDeclarationHTML, ACADEMIC_RECORD.getFolderPath(),
                ACADEMIC_RECORD.getFilePrefix());

    }

    private EmailDetails buildEmailDetails(StudentDataUEG studentData, String pdfPath) {
        return new EmailDetails(studentData.getFirstName(), studentData.getEmail(),
                "HISTÓRICO ACADÊMICO UEG",
                "Olá, segue em anexo seu Histórico Acadêmico da UEG",
                "Histórico_Acadêmico", pdfPath);
    }

    private String sendEmail(IPlataformService plataformService, EmailDetails emailDetails) {
        if (plataformService.sendEmailWithFileAttachment(emailDetails)) {
            return "Seu Histórico Acadêmico foi enviado para o seu e-mail acadêmico.";
        }
        return "Houve um erro ao enviar seu Histórico Acadêmico, tente novamente mais tarde";
    }

}
