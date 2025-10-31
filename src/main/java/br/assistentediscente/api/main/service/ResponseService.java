package br.assistentediscente.api.main.service;

import br.assistentediscente.api.institutionplugin.ueg.converter.ParameterTool;
import br.assistentediscente.api.institutionplugin.ueg.converter.Tool;
import br.assistentediscente.api.integrator.converter.IBaseTool;
import br.assistentediscente.api.integrator.converter.IResponseTool;
import br.assistentediscente.api.integrator.enums.ParameterType;
import br.assistentediscente.api.integrator.enums.WeekDay;
import br.assistentediscente.api.integrator.exceptions.ai.DisciplineNameNotFoundException;
import br.assistentediscente.api.integrator.exceptions.intent.IntentNotSupportedException;
import br.assistentediscente.api.integrator.exceptions.intent.ScheduleWeekEmptyOrNotFoundException;
import br.assistentediscente.api.integrator.exceptions.param.ParamNotFoundException;
import br.assistentediscente.api.integrator.exceptions.serviceplugin.ParameterTypeNotSupported;
import br.assistentediscente.api.integrator.exceptions.student.StudentNotAuthenticatedException;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;
import br.assistentediscente.api.integrator.institutions.KeyValue;
import br.assistentediscente.api.integrator.institutions.info.IAcademicData;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineAbsence;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineGrade;
import br.assistentediscente.api.integrator.institutions.info.IDisciplineSchedule;
import br.assistentediscente.api.integrator.plataformeservice.IPlataformService;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;
import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;
import br.assistentediscente.api.main.dto.AutenticationResponse;
import br.assistentediscente.api.main.dto.InstitutionLoginFieldsDTO;
import br.assistentediscente.api.main.dto.ResponseToolDTO;
import br.assistentediscente.api.main.dto.SkillResponse;
import br.assistentediscente.api.main.formatter.absence.TotalAbsencesByDisciplineFormatter;
import br.assistentediscente.api.main.formatter.available.AvailableActionsFormatter;
import br.assistentediscente.api.main.formatter.grade.impl.GradeByDisciplineNameFormatter;
import br.assistentediscente.api.main.formatter.grade.impl.GradeBySemesterFormatter;
import br.assistentediscente.api.main.formatter.overallaverage.OverallAverageFormatter;
import br.assistentediscente.api.main.formatter.schedule.impl.ScheduleByDayResponseFormatter;
import br.assistentediscente.api.main.formatter.schedule.impl.ScheduleByDisciplineNameFormatter;
import br.assistentediscente.api.main.model.IStudent;
import br.assistentediscente.api.main.model.impl.Institution;
import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.plataformservice.PlataformServiceImpl;
import br.assistentediscente.api.main.reflection.Reflection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.assistentediscente.api.integrator.exceptions.UtilExceptionHandler.handleException;

@Service
public class ResponseService extends Reflection{

    private final MessageSource messageSource;
    private final InstitutionService baseInstitutionService;
    private final StudentService studentService;
    private final AIService aiService;
    @Value("${institution.package}")
    private String institutionPackage;
    private final IPlataformService plataformService;
    private final PlannerService plannerService;

    public ResponseService(MessageSource messageSource, InstitutionService baseInstitutionService, PlannerService plannerService,
                           StudentService studentService, AIService aiService, PlataformServiceImpl plataformService) {
        this.messageSource = messageSource;
        this.baseInstitutionService = baseInstitutionService;
        this.studentService = studentService;
        this.aiService = aiService;
        this.plataformService = plataformService;
        this.plannerService = plannerService;
    }

    /**
     * Principal metodo, irá receber os parametros do controller, instanciar a universidade e chamar o
     * metodo responsavel para lidar com o intent
     * @param intent Qual o serviço/resposta esperada pela Alexa
     * @param externalID Key do usuario, será obtida através do token JWT para identificar o aluno/instituição
     * @param parameters Parametros usados para busca/filtro
     * @return Resposta pronta para ser respondida pela Alexa
     */
    public SkillResponse doResponseByIntent(String intent, String externalID, List<String> parameters) {
        IStudent student = studentService.findByExternalKey(UUID.fromString(externalID));
        try {
            IBaseInstitutionPlugin institutionPlugin = getInstitutionPlugin(institutionPackage, student);

            return new SkillResponse((String) invokeResponseMethodByIntent(intent, institutionPlugin, parameters));
        }catch (Exception exception){
            throw new RuntimeException((exception.getCause() != null) ? exception.getCause(): exception);
        }
    }

    /**
     * Metodo responsavel pela chamada e execução de serviços fora os padrões do sistema, podendo ser
     * proprios do sistema que criam informações apartir de dados ja compartilhados pelas instituições
     * sem que as mesmas tenham que criar novos, ou chamar serviços proprios de cada instituição
     * que o sistema não tem conhecimento previo
     * @param activationService nome de ativação do serviço que o usuario enviou
     * @param externalID Key do usuario, será obtida através do token JWT para identificar o aluno/instituição
     * @param parameters Parametros usados pelo serviço, fornecidos pelo usuario
     * @return Resposta para o usuario de acordo com a execução do serviço
     */
    public SkillResponse callService(String activationService, String externalID,
                                     Map<String,String> parameters) {
        IStudent student = studentService.findByExternalKey(UUID.fromString(externalID));
        try {
            IBaseInstitutionPlugin institutionPlugin = getInstitutionPlugin(institutionPackage, student);

            return new SkillResponse(invokeServicePlugin(activationService,institutionPlugin, parameters,
                    aiService, plataformService));
        }catch (Exception exception){
            throw new RuntimeException((exception.getCause() != null) ? exception.getCause(): exception);
        }
    }

    /**
     * Metodo responsavel por realizar a autenticação do estudante na sua instituição de ensino
     * @param login campo de login do estudante
     * @param password campo de senha do estudante
     * @param institutionName nome da instituição de ensino conforme o que está salvo no bd
     * @return External Key para ser usada nos tokens JWT
     */
    public AutenticationResponse authenticateStudent(String login, String password, String institutionName) {
        try {
            Institution baseInstitution = baseInstitutionService.getInstitutionByInstitutionName(institutionName);
            IBaseInstitutionPlugin institutionPlugin = getInstitutionPlugin(institutionPackage,baseInstitution);
            List<KeyValue> studentAccessData = institutionPlugin.authenticateStudent(login, password);
            Student student = studentService.create(studentAccessData, baseInstitution);
            return new AutenticationResponse(student.getExternalKey().toString(), baseInstitution.getId());
        }catch (RuntimeException e ){
            handleException(e, new StudentNotAuthenticatedException());
            return null;
        }
    }

    /**
     * Metodo utilizado para invocar o metodos corretos de acordo com o intent recebido pela Alexa
     * @param intent Nome do intent/metodo que deve ser chamado
     * @param institutionPlugin plugin da instituição
     * @param parameters lista de parametros
     * @return InstitutionLoginFieldsDTO
     * @throws Exception Erros que podem vir a serem causados por diferentes erros, geralmente será lançado BusinessException
     */
    private Object invokeResponseMethodByIntent(String intent, IBaseInstitutionPlugin institutionPlugin,
                                                List<String> parameters) throws Exception {
        String methodName = "get" +
                intent.substring(0, 1).toUpperCase() +
                intent.substring(1) +
                "Response";
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, IBaseInstitutionPlugin.class, List.class);
            return method.invoke(this, institutionPlugin, parameters);
        } catch (Exception e) {
            methodName = methodName.substring(methodName.indexOf("get") + 3, methodName.indexOf("Response"));
            handleException(e, new RuntimeException("Method "+ methodName +" not found"));
        }
        return null;
    }

    private static void verifyAndThrowMethodNotFoundException(Exception e) {
        String[] errorsString = e.getLocalizedMessage().split("\\.");
        for (String errorString : errorsString) {
            if (errorString.contains("get") && errorString.contains("Response")) {
                errorString = errorString.substring(errorString.indexOf("get") + 3, errorString.indexOf("Response"));
                throw new RuntimeException("Method " + errorString + " not found");
            }
        }
    }

    /**
     * Metodo para realizar o refresh dos tokens de acesso do estudante para garantir que os request de
     * futuras solicitações continuem funcionando
     * @param student o estudante que terá o token atualizado
     */
    public void getRefreshedAccessData(Student student) {
        try {
            IBaseInstitutionPlugin institutionRequest = getInstitutionPlugin(institutionPackage, student);
            List<KeyValue> refreshedAccessData = institutionRequest.refreshStudentAccessData(student.getKeyValueList());
            studentService.refreshAccessData(student, refreshedAccessData);
        }catch (Exception e){
            throw new RuntimeException("Method invocation failed", e);
        }
    }

    /**
     * Metodo para trazer os nomes dos campos de login a serem apresentados na tela de login do auth server
     * @param institutionName nome da instuição
     * @return InstitutionLoginFieldsDTO contendo os nomes dos campos a serem apresentados
     */
    public InstitutionLoginFieldsDTO getInstitutionLoginFields(String institutionName) {
        return baseInstitutionService.getInstitutionLoginFields(institutionName);
    }

    //Não implementado UEG, apenas para exceção
    private String getAbsenceByDisciplineAndDateResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters){
        try {
            return institutionPlugin.getAbsenceByDisciplineAndDate(parameters.get(0), LocalDate.now()).toString();
        }catch (IntentNotSupportedException e){
            throw new IntentNotSupportedException();
        }
    }

    /**
     * Usado para verificar se todos os parametros necessarios estão presentes
     * @param parameters lista de parametros
     * @param quantityParameters quantidades de parametros
     */
    private static void checkAllParametersExist(List<String> parameters, int quantityParameters) {
        if(parameters == null || parameters.isEmpty() || parameters.size() != quantityParameters){
            throw new ParamNotFoundException();
        }
    }

    /**
     * Metodo para buscar as aulas do estudante de acordo com o dia/data recebido por parametro
     * @param institutionPlugin plugin da instituição
     * @param parameters data do dia
     * @return frase de resposta para Alexa
     */
    private String getScheduleByDayResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters) {
        checkAllParametersExist(parameters, 1);
        ScheduleByDayResponseFormatter responseFormatter = new ScheduleByDayResponseFormatter(messageSource);
        WeekDay day = WeekDay.getByDate(parameters.get(0));

        return responseFormatter.doResponse(
                day.getShortName(), institutionPlugin.getScheduleByWeekDay(day));
    }

    /**
     * Metodo para buscar as aulas do estudante de acordo com o nome da disciplina recebido por parametro
     * @param institutionPlugin plugin da instituição
     * @param parameters nome da disciplina
     * @return frase de resposta para Alexa
     */
    private String getScheduleByDisciplineNameResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters) {
        checkAllParametersExist(parameters, 1);

        List<IDisciplineSchedule> disciplinesSchedule = institutionPlugin.getWeekSchedule();

        if (disciplinesSchedule == null || disciplinesSchedule.isEmpty()){
            throw new ScheduleWeekEmptyOrNotFoundException();
        }

        String disciplineToGetSchedule = aiService.getDisciplineNameFromAIWithDiscipline(parameters, disciplinesSchedule);

        verifyDisciplineName(disciplineToGetSchedule, parameters.get(0));

        ScheduleByDisciplineNameFormatter responseFormatter = new ScheduleByDisciplineNameFormatter(messageSource);
        return responseFormatter.doResponse(disciplineToGetSchedule,
                institutionPlugin.getScheduleByDisciplineName(disciplineToGetSchedule));
    }

    /**
     * Metodo para buscar as notas de determina disciplina recebida por parametro
     * @param institutionPlugin plugin da instituição
     * @param parameters nome da disciplina que deve ter suas notas buscadas
     * @return frase de resposta para Alexa
     */
    private String getGradeByDisciplineNameResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters) {
        checkAllParametersExist(parameters, 1);

        List<IDisciplineGrade> disciplineGrades = institutionPlugin.getGrades();

        String disciplineToGetGrades = aiService.getDisciplineNameFromAIWithDiscipline(parameters, disciplineGrades);

        verifyDisciplineName(disciplineToGetGrades, parameters.get(0));

        GradeByDisciplineNameFormatter formatter = new GradeByDisciplineNameFormatter(messageSource);
        return formatter.doResponse(disciplineToGetGrades, disciplineGrades);
    }

    /**
     * Metodo para busca das notas de determinado semestre recebido por parametro
     * @param institutionPlugin pluign da instiuição
     * @param parameters semestre que deve ter suas notas apresentadas, ex: 2022/2
     * @return frase de resposta para Alexa
     */
    private String getGradeBySemesterResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters) {
        checkAllParametersExist(parameters, 1);

        List<IDisciplineGrade> semesterGrades = institutionPlugin.getGradesBySemester(parameters.get(0));

        GradeBySemesterFormatter formatter = new GradeBySemesterFormatter(messageSource);

        return formatter.doResponse(parameters.get(0), semesterGrades);
    }

    /**
     * Metodo para trazer a media geral do curso do estudante
     * @param institutionPlugin plugin da instituição
     * @param parameters esse metodo não precisa de parametros
     * @return frase de resposta para Alexa
     */
    private String getOverallAverageResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters) {

        IAcademicData academicData = institutionPlugin.getAcademicData();

        OverallAverageFormatter formatter = new OverallAverageFormatter(messageSource);
        return formatter.doResponse(academicData);
    }

    private String getTotalAbsencesByDisciplineNameResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters) {

        checkAllParametersExist(parameters, 1);

        List<IDisciplineAbsence> disciplinesToAbsences = institutionPlugin.getActiveDisciplinesWithAbsences();

        String disciplineToGetAbsence = aiService.getDisciplineNameFromAIWithDiscipline(parameters, disciplinesToAbsences);

        verifyDisciplineName(disciplineToGetAbsence, parameters.get(0));

        TotalAbsencesByDisciplineFormatter formatter = new TotalAbsencesByDisciplineFormatter(messageSource);
        return formatter.doResponse(disciplineToGetAbsence, disciplinesToAbsences);
    }

    private String getAvailableActionsResponse(IBaseInstitutionPlugin institutionPlugin, List<String> parameters){

        List<String> institutionServiceNames = getFirstActivationNameForServices(
                institutionPlugin.getAllServicePlugins());

        AvailableActionsFormatter formatter = new AvailableActionsFormatter(messageSource);
        return formatter.doResponse(institutionServiceNames);
    }

    private void verifyDisciplineName(String aiResponseDisciplineName, String disciplineNameIntent){
        if (aiResponseDisciplineName == null || aiResponseDisciplineName.isEmpty() ||
                aiResponseDisciplineName.trim().equalsIgnoreCase("NENHUMA")){
            throw new DisciplineNameNotFoundException(new Object[]{disciplineNameIntent});

        }
    }

    public IResponseTool doResponseByToolName(String toolName, String externalID, Map<String, String> parameters) {
        if (externalID != null) {
            return responseToolAuthenticated(toolName, externalID, parameters);
        } else {
            return responseToolFreeAccess(toolName, parameters);
        }
    }

    public IResponseTool responseToolAuthenticated(String toolName, String externalID, Map<String, String> parameters) {
        IStudent student = studentService.findByExternalKey(UUID.fromString(externalID));
        try {
            IBaseInstitutionPlugin institutionPlugin = getInstitutionPlugin(institutionPackage, student);

            List<IBaseTool> toolsList = institutionPlugin.getAllInformationToolsPlugins();
            toolsList.addAll(this.getAllServiceToolsPlugins(institutionPlugin));
            toolsList.addAll(plannerService.getToolsPlanner());

            IBaseTool toolForExecute = toolsList.stream().filter(tool -> tool.getName().equals(toolName)).findFirst().orElse(null);

            if (toolForExecute == null) {
                throw new RuntimeException("Tool not found");
                
            } else if (toolForExecute.getServiceClass() != null) {
                this.mountParameters(toolForExecute, parameters, institutionPlugin);
                IServicePlugin serviceObject = toolForExecute.getServiceClass().getDeclaredConstructor().newInstance();
                String result = invokeServicePlugin(serviceObject, institutionPlugin, parameters, plataformService);
                return new ResponseToolDTO("Serviço ativado e concluído com sucesso!", result);
                
            } else {
                return invokeResponseMethodByTool(toolForExecute, parameters, institutionPlugin);
                
            }
        }catch (Exception exception){
            throw new RuntimeException((exception.getCause() != null) ? exception.getCause(): exception);
        }
    }

    public IResponseTool responseToolFreeAccess(String toolName, Map<String, String> parameters) {
        try {
            List<IBaseTool> toolsList = getInformationTools();
            IBaseTool toolForExecute = toolsList.stream().filter(tool -> tool.getName().equals(toolName)).findFirst().orElse(null);
            
            if (toolForExecute == null) {
                throw new RuntimeException("Tool not found");
            }else {
                return invokeResponseMethodByTool(toolForExecute, parameters, null);
            }
        }catch (Exception exception){
            throw new RuntimeException((exception.getCause() != null) ? exception.getCause(): exception);
        }
    }

    private IResponseTool invokeResponseMethodByTool(IBaseTool tool, Map<String, String> parameters, IBaseInstitutionPlugin institutionPlugin) throws Exception {
        try {
            this.mountParameters(tool, parameters, institutionPlugin);

            return tool.getExecuteMethod().execute(parameters);
        } catch (Exception e) {
            handleException(e, new RuntimeException(e.getMessage()));
        }
        return null;
    }

    private void mountParameters(IBaseTool tool, Map<String, String> parameters, IBaseInstitutionPlugin institutionPlugin) {
        if (tool.getParameters() != null && !tool.getParameters().isEmpty()) {
            for (String keyParameter: tool.getParameters().keySet()) {
                AParameter parameter = tool.getParameters().get(keyParameter);
                switch (parameter.getType()) {
                    case AUTO -> {
                        if (institutionPlugin != null && parameter.getValueFromInstitution(institutionPlugin) instanceof String) {
                            parameters.put(keyParameter, parameter.getValueFromInstitution(institutionPlugin).toString());
                        } else if (parameter.getAutoValueParamMethod() != null){
                            parameters.put(keyParameter, parameter.getAutoValueParamMethod().getAutoValue());
                        }
                    }
                    case FIXED -> {
                        if (parameter.getDefaultValue() instanceof String) {
                            parameters.put(keyParameter, parameter.getDefaultValue().toString());
                        }
                    }
                    case OPTIONAL, MANDATORY -> {
                        if (parameter.getType().equals(ParameterType.MANDATORY) && parameters.get(keyParameter) == null) {
                            throw new ParamNotFoundException(Arrays.asList(keyParameter));
                        }

                        if (parameter.getNormalizationMethod() != null) {
                            parameters.put(keyParameter, parameter.getNormalizationMethod().normalize(parameters.get(keyParameter)));
                        }
                        if (parameter.getPossibleValuesMethod() != null) {
                            String itemFound = this.aiService.getItemInList(parameters.get(keyParameter), parameter.getPossibleValuesMethod().getPossibleValues());
                            parameters.put(keyParameter, itemFound);
                        }
                    }
                    default -> throw new ParameterTypeNotSupported();
                }
            }
        }
    }

    public List<IBaseTool> getAllToolsPlugins(String externalID){
        IStudent student = studentService.findByExternalKey(UUID.fromString(externalID));
        try {
            IBaseInstitutionPlugin institutionPlugin = getInstitutionPlugin(institutionPackage, student);

            List<IBaseTool> tools = Stream.concat(institutionPlugin.getAllInformationToolsPlugins().stream(), this.getAllServiceToolsPlugins(institutionPlugin).stream()).collect(Collectors.toCollection(ArrayList::new));
            tools.addAll(plannerService.getToolsPlanner());

            return tools;
        }catch (Exception exception){
            exception.printStackTrace();
            throw new RuntimeException((exception.getCause() != null) ? exception.getCause(): exception);
        }
    }

    public List<IBaseTool> getAllServiceToolsPlugins(IBaseInstitutionPlugin institutionPlugin) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<IBaseTool> tools = new ArrayList<>();
        for (Class<? extends IServicePlugin> serviceClass : institutionPlugin.getAllServicePlugins()) {
            IServicePlugin serviceObject = serviceClass.getDeclaredConstructor().newInstance();

            Map<String, AParameter> parameters = new HashMap<>();
            for (AParameter parameter : serviceObject.getParameters()) {
                ParameterTool parameterTool = ParameterTool.builder().clazz(parameter.getClazz()).type(parameter.getType()).description(parameter.getDescription()).build();
                parameters.put(parameter.getName(), parameterTool);
            }

            Tool serviceTool = Tool.builder()
                    .name(serviceObject.getName())
                    .description(serviceObject.getDescription())
                    .parameters(parameters)
                    .serviceClass(serviceClass)
                    .highConfirmation(true)
                    .build();
            tools.add(serviceTool);
        }
        return tools;
    }

    public List<IBaseTool> getInformationTools(){
        try {
            return baseInstitutionService.getAllInstitution().stream()
                    .map(inst -> getInstitutionPlugin(institutionPackage, inst))
                    .flatMap(plugin -> plugin.getAllInformationToolsPlugins().stream())
                    .filter(tool -> !tool.getAuthenticationRequired())
                    .toList();
        }catch (Exception exception){
            exception.printStackTrace();
            throw new RuntimeException((exception.getCause() != null) ? exception.getCause(): exception);
        }
    }
}
