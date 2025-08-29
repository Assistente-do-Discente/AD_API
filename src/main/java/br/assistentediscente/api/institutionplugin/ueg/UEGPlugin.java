package br.assistentediscente.api.institutionplugin.ueg;

import br.assistentediscente.api.institutionplugin.ueg.converter.ConverterUEG;
import br.assistentediscente.api.institutionplugin.ueg.converter.ParameterTool;
import br.assistentediscente.api.institutionplugin.ueg.converter.Tool;
import br.assistentediscente.api.institutionplugin.ueg.dto.KeyUrl;
import br.assistentediscente.api.institutionplugin.ueg.formatter.FormatterScheduleByDisciplineName;
import br.assistentediscente.api.institutionplugin.ueg.formatter.FormatterScheduleByWeekDay;
import br.assistentediscente.api.institutionplugin.ueg.infos.StudentDataUEG;
import br.assistentediscente.api.integrator.converter.IBaseTool;
import br.assistentediscente.api.integrator.converter.IConverterInstitution;
import br.assistentediscente.api.integrator.enums.ClazzType;
import br.assistentediscente.api.integrator.enums.ParameterType;
import br.assistentediscente.api.integrator.enums.WeekDay;
import br.assistentediscente.api.integrator.exceptions.UtilExceptionHandler;
import br.assistentediscente.api.integrator.exceptions.institution.InstitutionComunicationException;
import br.assistentediscente.api.integrator.exceptions.intent.IntentNotSupportedException;
import br.assistentediscente.api.integrator.exceptions.student.StudentNotAuthenticatedException;
import br.assistentediscente.api.integrator.exceptions.student.StudentNotFoundException;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;
import br.assistentediscente.api.integrator.institutions.KeyValue;
import br.assistentediscente.api.integrator.institutions.info.*;
import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;
import br.assistentediscente.api.main.model.IStudent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class UEGPlugin implements IBaseInstitutionPlugin, UEGEndpoint {

    private CookieStore httpCookieStore;
    private final HttpClientContext localContext;
    private final CloseableHttpClient httpClient;
    private String acuId;
    private final IConverterInstitution converterUEG;

    public UEGPlugin() {
        this.httpCookieStore = new BasicCookieStore();
        this.localContext = HttpClientContext.create();
        this.httpClient =
                HttpClients.custom().setDefaultCookieStore(httpCookieStore).build();
        this.converterUEG = new ConverterUEG();
    }

    public UEGPlugin(IStudent student) {
        setStudentAccessData(student.getKeyValueList());
        this.localContext = HttpClientContext.create();
        this.httpClient =
                HttpClients.custom()
                        .setDefaultCookieStore(httpCookieStore).build();
        this.converterUEG = new ConverterUEG();
    }

    /**
     * Verifica se o cpf e senha estão preenchidos, se sim
     * Realiza um request para realizar login no sistema
     *
     * @return retorna uma lista de KeyValue com os cookies de conexão do estudante
     */
    @Override
    public List<KeyValue> authenticateStudent(String username, String password) {

        HttpPost httpPost = new HttpPost(VALIDA_LOGIN);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("cpf", username));
        nvps.add(new BasicNameValuePair("senha", password));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost, localContext);
            HttpEntity entity = httpResponse.getEntity();
            String hmtlResponse = EntityUtils.toString(entity);
            responseLoginOK(hmtlResponse);
            return cookiesToKeyValue();
        } catch (Exception error) {
            UtilExceptionHandler.handleException(error, new InstitutionComunicationException(error));
        }
        return null;
    }

    private void responseLoginOK(String hmtlResponse) {
        if (!hmtlResponse.contains("Bem Vindo ao ADMS!")){
            throw new StudentNotAuthenticatedException();
        }
    }

    private List<KeyValue> cookiesToKeyValue() {

        List<KeyValue> studentAccessData = new ArrayList<>();
        for (Cookie cookie : this.getHttpCookieStore().getCookies()){
            KeyValue cookies = new KeyValue();
            cookies.setKey(cookie.getName());
            cookies.setValue(cookie.getValue());
            studentAccessData.add(cookies);
        }
        return studentAccessData;
    }

    @Override
    public void setStudentAccessData(List<KeyValue> accessDataList) {
        this.httpCookieStore = new BasicCookieStore();
        for (KeyValue accessData : accessDataList){
            BasicClientCookie basicClientCookie = new BasicClientCookie(accessData.getKey(), accessData.getValue());
            basicClientCookie.setDomain("www.app.ueg.br");
            basicClientCookie.setPath("/");
            this.httpCookieStore.addCookie(basicClientCookie);
        }
    }

    @Override
    public List<IDisciplineSchedule> getScheduleByWeekDay(WeekDay weekDay){
        HttpGet httpGet = new HttpGet(HORARIO_AULA);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                String entityString = EntityUtils.toString(entity);
                if (entityString == null || entityString.isEmpty()) return null;
                FormatterScheduleByWeekDay formatter = new FormatterScheduleByWeekDay();
                return formatter.disciplinesWithScheduleByDay(weekDay,
                        converterUEG.getDisciplinesWithScheduleFromJson
                                ((JsonArray) JsonParser.parseString(entityString))
                );
            } else
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");

        } catch (Exception error) {
            throw new InstitutionComunicationException("Ocorreu um problema na obtenção do horario," +
                    " tente novamente mais tarde");
        }
    }


    @Override
    public Integer getAbsenceByDisciplineAndDate(String disciplineName, LocalDate date) throws IntentNotSupportedException {
        throw new IntentNotSupportedException();
    }

    @Override
    public List<IDisciplineSchedule> getScheduleByDisciplineName(String disciplineToGetSchedule){
        HttpGet httpGet = new HttpGet(HORARIO_AULA);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                String entityString = EntityUtils.toString(entity);
                if (entityString == null || entityString.isEmpty()) return null;
                FormatterScheduleByDisciplineName formatter = new FormatterScheduleByDisciplineName();
                return formatter.scheduleByDisciplineName(disciplineToGetSchedule, converterUEG.getDisciplinesWithScheduleFromJson
                        ((JsonArray) JsonParser.parseString(entityString))
                );
            } else
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");

        } catch (Exception error) {
            throw new InstitutionComunicationException("Ocorreu um problema na obtenção do horário, " +
                    "tente novamente mais tarde");
        }
    }

    @Override
    public List<IDisciplineSchedule> getWeekSchedule() throws IntentNotSupportedException {
        HttpGet httpGet = new HttpGet(HORARIO_AULA);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                String entityString = EntityUtils.toString(entity);
                if (entityString == null || entityString.isEmpty()) return null;
                return converterUEG.getDisciplinesWithScheduleFromJson
                        ((JsonArray) JsonParser.parseString(entityString)
                        );
            } else
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG, " +
                        "tente novamente mais tarde");

        } catch (Exception error) {
            throw new InstitutionComunicationException("Ocorreu um problema na obtenção do horario," +
                    " tente novamente mais tarde");
        }
    }


    @Override
    public List<KeyValue> refreshStudentAccessData(List<KeyValue> accessData) {
        enterPortalEstudante();
        return cookiesToKeyValue();
    }

    /**
     * Executa um request para entrar no portal do Estudante, caso o response seja 200 chama um metodo para obter o
     * acu_id do estudante, utilizado em outras requisições
     */
    private void enterPortalEstudante() {
        HttpGet httpGet = new HttpGet(ENTRA_PORTAL_ESTUDANTE);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            if (responseOK(httpResponse)) {
                getPersonId();
            }
        } catch (Throwable error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * Executa um request responsavel pelo retorno do acu_id do estudante e o armazenando na variavel idPessoa
     */
    public void getPersonId() {
        acuId = getStudentData().getPersonId();
    }

    /**
     * Executa um request para obter as notas das diciplinas do estudante
     * Necessario que se tenha obtido o id da pessoa antes de executar
     * Caso tenha um response 200, preenche a variavel jsonGrade com as notas e diciplinas em formato Json
     *
     */
    public List<IDisciplineGrade> getGrades() throws IntentNotSupportedException {
        if (Objects.isNull(acuId)) getPersonId();

        HttpGet httpGet = new HttpGet(DADOS_DISCIPLINAS + acuId);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                return converterUEG.getGradesWithDetailedGradeFromJson(
                        (JsonArray) JsonParser.parseString(EntityUtils.toString(entity)));
            }else{
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");
            }

        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    "tente novamente mais tarde");
        }
    }

    @Override
    public List<IDisciplineGrade> getGradesBySemester(String semester)
            throws IntentNotSupportedException, InstitutionComunicationException {

        if (Objects.isNull(acuId)) getPersonId();
        HttpGet httpGet = new HttpGet(DADOS_DISCIPLINAS + acuId);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                return converterUEG.getGradesBySemesterFromJson(
                        (JsonArray) JsonParser.parseString(EntityUtils.toString(entity)), semester);
            }else{
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");
            }

        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    "tente novamente mais tarde");
        }
    }

    @Override
    public IAcademicData getAcademicData() throws IntentNotSupportedException, InstitutionComunicationException {
        HttpGet httpGet = new HttpGet(DADOS_ACADEMICOS);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                return converterUEG.getAcademicDataFromJson(JsonParser.parseString(EntityUtils.toString(entity)));
            }else{
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");
            }

        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    "tente novamente mais tarde");
        }
    }

    @Override
    public List<IDisciplineAbsence> getActiveDisciplinesWithAbsences() throws IntentNotSupportedException, InstitutionComunicationException {
        if (Objects.isNull(acuId)) getPersonId();

        HttpGet httpGet = new HttpGet(DADOS_DISCIPLINAS + acuId);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                return converterUEG.
                        getDisciplinesWithAbsencesFromJson((JsonArray) JsonParser.parseString(EntityUtils.toString(entity)));

            }else{
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");
            }

        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    "tente novamente mais tarde");
        }
    }

    public IStudentData getStudentData() throws IntentNotSupportedException, InstitutionComunicationException {

        HttpGet httpGet = new HttpGet(PERFIL);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();
            if (responseOK(httpResponse)) {
                return converterUEG.getStudentDataFromJson(JsonParser.
                        parseString(EntityUtils.toString(entity)));
            }
            throw new StudentNotFoundException();
        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    " tente novamente mais tarde");
        }
    }

    /**
     * Verifica o response obtido
     * @return true caso 200
     */
    private boolean responseOK(CloseableHttpResponse httpResponse) {
        return httpResponse.getCode() == 200;
    }

    public Set<Class<? extends IServicePlugin>> getAllServicePlugins() {

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("br.assistentediscente.api.institutionplugin.ueg.serviceplugin"))
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage("br.assistentediscente.api.institutionplugin.ueg.serviceplugin")));

        return reflections.getSubTypesOf(IServicePlugin.class);
    }

    public String generateNewAttendanceDeclarationHTML(){

        HttpGet httpGet = new HttpGet(GERAR_NOVA_DECLARACAO_FREQUENCIA);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();
            if (responseOK(httpResponse)) {
                KeyUrl keyUrl = ((ConverterUEG)converterUEG).getKeyUrlFromJson(
                        JsonParser.parseString(EntityUtils.toString(entity)));

                if (Objects.nonNull(keyUrl) && !keyUrl.url().isEmpty()) {
                    return getHTMLFromURL(keyUrl.url());
                }
            }
            throw new InstitutionComunicationException("Não foi possível gerar sua declaração de frequencia," +
                    " tente novamente mais tarde");
        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    " tente novamente mais tarde");
        }

    }

    private String getHTMLFromURL(String url) {
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();
            if (responseOK(httpResponse)) {
                return EntityUtils.toString(entity);
            }
            throw new InstitutionComunicationException();
        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    " tente novamente mais tarde");
        }
    }

    public String generateNewAcademicRecordHTML(){

        try {
            HttpPost httpPost = new HttpPost(GERAR_NOVO_HISTORICO_ACADEMICO);
            StudentDataUEG studentDataUEG = (StudentDataUEG) getStudentData();
            String jwt = getJWT();
            getPre(jwt);

            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("acu_id", studentDataUEG.getPersonId()));
            nvps.add(new BasicNameValuePair("jwt", jwt));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            httpPost.setHeader("Authorization", "Bearer " + jwt);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost, localContext);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                KeyUrl keyUrl = ((ConverterUEG)converterUEG).getKeyUrlFromJson(
                        JsonParser.parseString(EntityUtils.toString(entity)));

                if (Objects.nonNull(keyUrl) && !keyUrl.url().isEmpty()) {
                    return getHTMLFromURL(keyUrl.url());
                }
            }
            throw new InstitutionComunicationException("Não foi possível gerar sua declaração de frequencia," +
                    " tente novamente mais tarde");
        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    " tente novamente mais tarde");
        }

    }

    private String getJWT() {
        HttpGet httpGet = new HttpGet(GET_JWT_TOKEN);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();
            if (responseOK(httpResponse)) {
                return ((ConverterUEG)converterUEG).getTokenFromJson(
                        JsonParser.parseString(EntityUtils.toString(entity))).jwt();
            }
            throw new InstitutionComunicationException("Não foi possível gerar sua declaração de frequencia," +
                    " tente novamente mais tarde");
        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    " tente novamente mais tarde");
        }

    }

    private void getPre(String jwt) {
        HttpGet httpGet = new HttpGet(PRE_GERAR_NOVO_HISTORICO_ACADEMICO.replace("{0}", jwt));
        try {
            httpClient.execute(httpGet, localContext);
        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    " tente novamente mais tarde");
        }

    }

    public List<IBaseTool> getAllInformationToolsPlugins() {
        return List.of(
                Tool.tool(
                        "getSchedule",
                        "Obter o horário de aulas",
                        this::getSchedules
                ),
                Tool.tool(
                        "getScheduleByWeekDay",
                        "Obter o horário de aulas do dia informado",
                        this::getSchedules,
                        Map.of(
                                "weekDay",
                                ParameterTool.enumParam(
                                        "O dia da semana, como segunda-feira ou sábado",
                                        WeekDay.values(),
                                        WeekDay::getShortName
                                )
                        )
                ),
                Tool.tool(
                        "getScheduleByDisciplineName",
                        "Obter o horário de aula da disciplina informada",
                        this::getSchedules,
                        Map.of(
                                "disciplineName",
                                ParameterTool.stringParam("O nome da disciplina", ParameterType.MANDATORY)
                        )
                ),
                Tool.tool(
                        "getGrades",
                        "Obter as notas do estudante",
                        this::getGrades
                ),
                Tool.tool(
                        "getAcademicData",
                        "Obter dados sobre a integralização do estudante no curso",
                        this::getAcademicData
                ),
                Tool.tool(
                        "getStudentData",
                        "Obter dados sobre o estudante",
                        this::getStudentData
                )
        );
    }

//    public List<IBaseTool> getAllInformationToolsPlugins() {
//        List<IBaseTool> tools = new ArrayList<>();
//        Map<String, AParameter> parameters = new HashMap<>();
//
//        ParameterTool parameterTool = ParameterTool.builder().clazz(ClazzType.ENUM).type(ParameterType.MANDATORY).description("O dia da semana, como segunda-feira ou sábado").possibleValues(Arrays.stream(WeekDay.values()).map(WeekDay::getShortName).collect(Collectors.toList())).build();
//        parameters.put("weekDay", parameterTool);
//        Tool getScheduleByWeekDayTool = Tool.builder().name("getScheduleByWeekDay").description("Obter o horaráio de aulas do dia informado").parameters(parameters).executeMethod(this::getSchedules).build();
//        tools.add(getScheduleByWeekDayTool);
//
//        Map<String, AParameter> parameters2 = new HashMap<>();
//        ParameterTool parameterTool2 = ParameterTool.builder().clazz(ClazzType.STRING).type(ParameterType.MANDATORY).description("O nome da disciplina").build();
//        parameters2.put("disiciplineName", parameterTool2);
//        Tool getScheduleByDisciplineNameTool = Tool.builder().name("getScheduleByDisciplineName").description("Obter o horaráio de aula da disciplina informada").parameters(parameters2).executeMethod(this::getSchedules).build();
//        tools.add(getScheduleByDisciplineNameTool);
//
//        Tool getGradesTool = Tool.builder().name("getGrades").description("Obter as notas do estudante").executeMethod(this::getGrades).build();
//        tools.add(getGradesTool);
//
//        Tool getAcademicDataTool = Tool.builder().name("getAcademicData").description("Obter dados sobre a integralização do estudante no curso").executeMethod(this::getAcademicData).build();
//        tools.add(getAcademicDataTool);
//
//        Tool getStudentDataTool = Tool.builder().name("getStudentData").description("Obter dados sobre o estudante").executeMethod(this::getStudentData).build();
//
//        tools.add(getStudentDataTool);
//
//        return tools;
//    }

    public Map<String, String> getSchedules(Map<String, String> parameters) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        response.put("response", mapper.writeValueAsString(getWeekSchedule()));
        return response;
    }

    public Map<String, String> getGrades(Map<String, String> parameters) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        response.put("response", mapper.writeValueAsString(getGrades()));
        return response;
    }

    public Map<String, String> getAcademicData(Map<String, String> parameters) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        response.put("response", mapper.writeValueAsString(getAcademicData()));
        return response;
    }

    public Map<String, String> getStudentData(Map<String, String> parameters) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        response.put("response", mapper.writeValueAsString(getStudentData()));
        return response;
    }
}
