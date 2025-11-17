package br.assistentediscente.api.institutionplugin.ueg;

import br.assistentediscente.api.institutionplugin.ueg.converter.ConverterUEG;
import br.assistentediscente.api.institutionplugin.ueg.converter.ParameterTool;
import br.assistentediscente.api.institutionplugin.ueg.converter.ResponseTool;
import br.assistentediscente.api.institutionplugin.ueg.converter.Tool;
import br.assistentediscente.api.institutionplugin.ueg.dto.KeyUrl;
import br.assistentediscente.api.institutionplugin.ueg.formatter.FormatterMessageResponse;
import br.assistentediscente.api.institutionplugin.ueg.formatter.FormatterScheduleByDisciplineName;
import br.assistentediscente.api.institutionplugin.ueg.formatter.FormatterScheduleByWeekDay;
import br.assistentediscente.api.institutionplugin.ueg.infos.ComplementaryActivitiesUEG;
import br.assistentediscente.api.institutionplugin.ueg.infos.ExtensionActivitiesUEG;
import br.assistentediscente.api.institutionplugin.ueg.infos.StudentDataUEG;
import br.assistentediscente.api.integrator.converter.IBaseTool;
import br.assistentediscente.api.integrator.converter.IConverterInstitution;
import br.assistentediscente.api.integrator.converter.IResponseTool;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Getter
public class UEGPlugin implements IBaseInstitutionPlugin, UEGEndpoint {

    private CookieStore httpCookieStore;
    private final HttpClientContext localContext;
    private final CloseableHttpClient httpClient;
    private String acuId;
    private final IConverterInstitution converterUEG;
    private FormatterMessageResponse formatterResponse;

    public UEGPlugin() {
        this.httpCookieStore = new BasicCookieStore();
        this.localContext = HttpClientContext.create();
        this.httpClient =
                HttpClients.custom().setDefaultCookieStore(httpCookieStore).build();
        this.converterUEG = new ConverterUEG();
        this.formatterResponse = new FormatterMessageResponse();
    }

    public UEGPlugin(IStudent student) {
        setStudentAccessData(student.getKeyValueList());
        this.localContext = HttpClientContext.create();
        this.httpClient =
                HttpClients.custom()
                        .setDefaultCookieStore(httpCookieStore).build();
        this.converterUEG = new ConverterUEG();
        this.formatterResponse = new FormatterMessageResponse();
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

    public List<IDisciplineGrade> getGradeByDisciplineName(String disciplineToGetGrade) {
        List<IDisciplineGrade> disciplineGrades = this.getGrades();
        return disciplineGrades.stream().filter(discipline -> disciplineToGetGrade.equalsIgnoreCase(discipline.getDisciplineName().toUpperCase())).toList();
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

    public List<IDisciplineAbsence> getActiveDisciplinesWithAbsencesByDisciplineName(String disciplineToGet) {
        List<IDisciplineAbsence> disciplineAbsence = this.getActiveDisciplinesWithAbsences();
        return disciplineAbsence.stream().filter(discipline -> disciplineToGet.equalsIgnoreCase(discipline.getDisciplineName().toUpperCase())).toList();
    }

    public List<IDisciplineAbsence> getActiveDisciplinesWithAbsencesBySemester(String semester) {
        List<IDisciplineAbsence> disciplineAbsence = this.getActiveDisciplinesWithAbsences();
        return disciplineAbsence.stream().filter(discipline -> semester.equalsIgnoreCase(discipline.getSemesterActive().toUpperCase())).toList();
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

    public ExtensionActivitiesUEG getExtensionActivities() throws IntentNotSupportedException {
        if (Objects.isNull(acuId)) getPersonId();

        HttpGet httpGet = new HttpGet(DADOS_ATV_COMP_EXT + acuId);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            HttpEntity entity = httpResponse.getEntity();

            if (responseOK(httpResponse)) {
                return new ConverterUEG().getExtensionActFromJson(JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject());
            }else{
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");
            }

        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    "tente novamente mais tarde");
        }
    }

    public ComplementaryActivitiesUEG getComplementaryActivities() throws IntentNotSupportedException {
        if (Objects.isNull(acuId)) getPersonId();

        HttpGet httpGet = new HttpGet(DADOS_ATIVIDADES_COMPLEMENTARES + acuId);
        HttpGet httpGet2 = new HttpGet(DADOS_ATV_COMP_EXT + acuId);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            CloseableHttpResponse httpResponse2 = httpClient.execute(httpGet2, localContext);
            HttpEntity entity = httpResponse.getEntity();
            HttpEntity entity2 = httpResponse2.getEntity();

            if (responseOK(httpResponse)) {
                return new ConverterUEG().getComplementaryActFromJson(JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject(), JsonParser.parseString(EntityUtils.toString(entity2)).getAsJsonObject());
            }else{
                throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                        " tente novamente mais tarde");
            }

        } catch (Throwable error) {
            throw new InstitutionComunicationException("Não foi possivel se comunicar com o servidor da UEG," +
                    "tente novamente mais tarde");
        }
    }

    public List<IBaseTool> getAllInformationToolsPlugins() {
        return new ArrayList<>(List.of(
                Tool.tool(
                        "getSchedule",
                        """
                        Use esta ferramenta para obter a grade de horários do estudante. Execute quando o usuário pedir informações sobre aulas, dias, horários, disciplinas, professores ou sala. O retorno inclui todas as disciplinas, horários por dia, professor, sala e intervalos consolidados. Utilize somente quando ficar claro que o usuário deseja consultar dados do cronograma acadêmico.
                        """,
                        this::getSchedules
                ),
                Tool.tool(
                        "getScheduleByWeekDay",
                        """
                        Use esta ferramenta para obter o horário de aulas de um dia específico. Utilize quando o usuário pedir as aulas de hoje, amanhã, ontem ou mencionar dias como segunda, terça, etc. O retorno inclui disciplinas, horários, professores e salas referentes apenas ao dia solicitado.
                        """,
        this::getSchedules,
                        Map.of(
                                "weekDay",
                                ParameterTool.enumParam(
                                        "O dia da semana solicitado pelo usuário, podendo ser segunda, terça, e outros.",
                                        WeekDay.values(),
                                        WeekDay::getShortName
                                )
                        )
                ),
                Tool.tool(
                        "getScheduleByDisciplineName",
                        """
                        Use esta ferramenta para consultar o horário de aula de uma disciplina específica. Utilize quando o usuário pedir o horário, o dia, o professor ou a sala de uma disciplina pelo nome. O retorno inclui todos os horários, professores e salas referentes somente à disciplina informada.
                        """,
                        this::getSchedules,
                        Map.of(
                                "disciplineName",
                                ParameterTool.stringParam("O nome da disciplina que o usuário deseja consultar", ParameterType.MANDATORY, null, this::getDisciplineNames)
                        )
                ),
                Tool.tool(
                        "getGrades",
                        """
                        Use esta ferramenta para consultar todas as notas do estudante em todas as disciplinas. O retorno inclui média final, avaliações detalhadas e semestre de cada disciplina.
                        """,
                        this::getGrades
                ),
                Tool.tool(
                        "getGradesByDisciplineName",
                        """
                        Use esta ferramenta para consultar as notas do estudante em uma disciplina específica. Utilize quando o usuário pedir a média, avaliações, pesos ou desempenho de uma matéria pelo nome. O retorno inclui média final, semestre e o detalhamento de cada avaliação da disciplina informada.
                        """,
                        this::getGrades,
                        Map.of(
                                "disciplineName",
                                ParameterTool.stringParam("O nome da disciplina para a qual o usuário deseja consultar as notas", ParameterType.MANDATORY, null, this::getDisciplineNames)
                        )
                ),
                Tool.tool(
                        "getGradesBySemester",
                        """
                        Use esta ferramenta para consultar todas as notas do estudante em um semestre específico. Utilize quando o usuário pedir as notas de um período como 2025/1 ou 2025/2. O retorno inclui cada disciplina cursada no semestre, com a média final e semestre e o detalhamento de cada avaliação da disciplina correspondente.
                        """,
                        this::getGrades,
                        Map.of(
                                "semester",
                                ParameterTool.stringParam("O semestre solicitado pelo usuário, no formato 'AAAA/N', por exemplo: '2025/2'.", ParameterType.MANDATORY, null, null)
                        )
                ),
                Tool.tool(
                        "calculateAverage",
                        """
                        Esta não é uma ferramenta de cálculo direto. Em vez disso, serve como instrução
                        para o agente saber como calcular a média UEG utilizando apenas ferramentas
                        matemáticas básicas disponíveis (adição, multiplicação e divisão).
                        
                        Fórmula da média UEG:
                          ((N1 * 2) + (N2 * 3)) / 5
                        
                        Como o agente deve proceder:
                        1. Multiplicar N1 por 2 usando a ferramenta de multiplicação.
                        2. Multiplicar N2 por 3 usando a ferramenta de multiplicação.
                        3. Somar os dois resultados usando a ferramenta de adição.
                        4. Dividir o valor obtido por 5 usando a ferramenta de divisão.
                        5. Retornar o resultado final como a média.
                        
                        Quando usar:
                        - Sempre que o usuário pedir para calcular a média UEG ou solicitar\s
                          "calcule minha média", "qual seria a média", "como fica minha média",\s
                          ou variações semelhantes.
                        
                        Importante:
                        - Esta ferramenta não retorna valores por si mesma.
                        - Ela apenas orienta o agente sobre o procedimento a seguir.
                        - A responsabilidade do cálculo é das ferramentas matemáticas básicas.
                        
                        Exemplo de Reposta:
                        - Tendo como N1 igual a 45 você precisa tirar 70 na N2 para atingir a média mínima de 60.
                        - Tendo como N2 igual a 70 você precisa tirar 45 na N2 para atingir a média mínima de 60.
                        """,
                        Map.of(
                                "nota1va", ParameterTool.numberParam(
                                        "Nota obtida na 1ª NA (0 a 100)", ParameterType.OPTIONAL, null, null
                                ),
                                "nota2va", ParameterTool.numberParam(
                                        "Nota obtida na 2ª NA (0 a 100)", ParameterType.OPTIONAL, null, null
                                ),
                                "media", ParameterTool.numberParam(
                                        "média (0 a 100)", ParameterType.OPTIONAL, null, null
                                )
                        )
                ),
                Tool.tool(
                        "getAcademicData",
                        """
                        Use esta ferramenta para consultar dados gerais do estudante no curso, incluindo média geral, situação acadêmica, percentual concluído e nome do curso. Utilize quando o usuário pedir sua média geral, progresso no curso ou situação acadêmica.
                        """,
                        this::getAcademicData
                ),
                Tool.tool(
                        "getStudentData",
                        """
                        Use esta ferramenta para consultar os dados básicos do estudante, como nome completo, e-mail institucional e identificador acadêmico. Utilize quando o usuário pedir informações pessoais relacionadas ao seu cadastro.
                        """,
                        this::getStudentData
                ),
                Tool.tool(
                        "getAbsences",
                        """
                        Use esta ferramenta para consultar o número de faltas do estudante em todas as disciplinas. Utilize quando o usuário pedir suas faltas gerais.
                        """,
                        this::getActiveDisciplinesWithAbsences
                ),
                Tool.tool(
                        "getAbsencesByDisciplineName",
                        """
                        Use esta ferramenta para consultar as faltas do estudante em uma disciplina específica. Utilize quando o usuário pedir o total de faltas ou faltas abonadas de uma matéria pelo nome.
                        """,
                        this::getActiveDisciplinesWithAbsences,
                        Map.of(
                                "disciplineName",
                                ParameterTool.stringParam("O nome da disciplina para a qual o usuário deseja consultar as faltas.", ParameterType.MANDATORY, null, this::getDisciplineNames)
                        )
                ),
                Tool.tool(
                        "getAbsencesBySemester",
                        """
                        Use esta ferramenta para consultar todas as faltas do estudante em um semestre específico. Utilize quando o usuário pedir as faltas referentes a períodos como 2025/1 ou 2025/2.
                        """,
                        this::getActiveDisciplinesWithAbsences,
                        Map.of(
                                "semester",
                                ParameterTool.stringParam("O semestre solicitado pelo usuário, no formato 'AAAA/N', por exemplo: '2025/2'.", ParameterType.MANDATORY, null, null)
                        )
                ),
                Tool.tool(
                        "getComplementaryActivities",
                        """
                        Use esta ferramenta para consultar todas as atividades complementares registradas pelo estudante. Utilize quando o usuário pedir suas horas cumpridas, horas necessárias, atividades aprovadas, pendentes ou o detalhamento de cada atividade realizada.
                        """,
                        this::getComplementaryActivities
                ),
                Tool.tool(
                        "getExtensionActivities",
                        """
                        Use esta ferramenta para consultar todas as atividades de extensão do estudante. Utilize quando o usuário pedir as horas cumpridas, horas exigidas, atividades aprovadas ou o detalhamento das ações de extensão registradas.
                        """,
                        this::getExtensionActivities
                ),
                Tool.tool(
                        "getAboutUeg",
                        """
                        Use esta ferramenta para obter informações institucionais sobre a UEG, incluindo sua história, fundamentos legais e dados gerais. Utilize quando o usuário pedir informações sobre a universidade.
                        """,
                        false,
                        this::getAboutUeg
                ),
                Tool.tool(
                        "getContactUeg",
                        """
                        Use esta ferramenta para consultar os contatos institucionais da UEG, incluindo telefones, e-mails e informações de setores específicos. Utilize quando o usuário pedir formas de contato com a universidade.
                        """,
                        false,
                        this::getContactUeg
                ),
                Tool.tool(
                        "verifyStudentIsAuthenticated",
                        """
                        Realiza verificação se o usuário está logado!
                        """,
                        false,
                        this::verifyStudentIsAuthenticated
                )
        ));
    }

    public IResponseTool getSchedules(Map<String, String> parameters) {
        List<IDisciplineSchedule> disciplineScheduleList;

        if (parameters.containsKey("weekDay")) {
            disciplineScheduleList = this.getScheduleByWeekDay(WeekDay.getByShortName(parameters.get("weekDay")));
            return new ResponseTool(formatterResponse.getScheduleByWeekDay(disciplineScheduleList, WeekDay.getByShortName(parameters.get("weekDay"))), disciplineScheduleList);
        } else if (parameters.containsKey("disciplineName")) {
            disciplineScheduleList = this.getScheduleByDisciplineName(parameters.get("disciplineName"));
            return new ResponseTool(formatterResponse.getScheduleByDisciplineName(disciplineScheduleList, parameters.get("disciplineName")), disciplineScheduleList);
        } else {
            disciplineScheduleList = this.getWeekSchedule();
            return new ResponseTool(formatterResponse.getSchedule(disciplineScheduleList), disciplineScheduleList);
        }
    }

    public IResponseTool getGrades(Map<String, String> parameters) {
        List<IDisciplineGrade> disciplineGrades;

        if (parameters.containsKey("semester")) {
            disciplineGrades = this.getGradesBySemester(parameters.get("semester"));
            return new ResponseTool(formatterResponse.getGradesBySemester(disciplineGrades, parameters.get("semester")), disciplineGrades);
        } else if (parameters.containsKey("disciplineName")) {
            disciplineGrades = this.getGradeByDisciplineName(parameters.get("disciplineName"));
            return new ResponseTool(formatterResponse.getGradesByDisciplineName(disciplineGrades, parameters.get("disciplineName")), disciplineGrades);
        } else {
            disciplineGrades = this.getGrades();
            return new ResponseTool(formatterResponse.getGrades(disciplineGrades), disciplineGrades);
        }
    }

    public IResponseTool getAcademicData(Map<String, String> parameters) {
        IAcademicData data = getAcademicData();
        return new ResponseTool(formatterResponse.getAcademicData(data), data);
    }

    public IResponseTool getStudentData(Map<String, String> parameters) {
        IStudentData studentData = getStudentData();
        return new ResponseTool(formatterResponse.getStudentData(studentData), studentData);
    }

    public IResponseTool getActiveDisciplinesWithAbsences(Map<String, String> parameters) {
        if (parameters.get("disciplineName") != null) {
            List<IDisciplineAbsence> disciplineAbsenceList = this.getActiveDisciplinesWithAbsencesByDisciplineName(parameters.get("disciplineName"));
            return new ResponseTool(formatterResponse.getActiveDisciplinesWithAbsencesByDisciplineName(disciplineAbsenceList, parameters.get("disciplineName")), disciplineAbsenceList);
        } if (parameters.get("semester") != null) {
            List<IDisciplineAbsence> disciplineAbsenceList = this.getActiveDisciplinesWithAbsencesBySemester(parameters.get("semester"));
            return new ResponseTool(formatterResponse.getActiveDisciplinesWithAbsencesBySemester(disciplineAbsenceList, parameters.get("semester")), disciplineAbsenceList);
        } else {
            List<IDisciplineAbsence> disciplineAbsenceList = this.getActiveDisciplinesWithAbsences();
            return new ResponseTool(formatterResponse.getActiveDisciplinesWithAbsences(disciplineAbsenceList), disciplineAbsenceList);
        }
    }

    public IResponseTool getComplementaryActivities(Map<String, String> parameters) {
        ComplementaryActivitiesUEG data = getComplementaryActivities();
        return new ResponseTool(formatterResponse.getComplementaryHours(data), data);
    }

    public IResponseTool getExtensionActivities(Map<String, String> parameters) {
        ExtensionActivitiesUEG data = getExtensionActivities();
        return new ResponseTool(formatterResponse.getExtensionHours(data), data);
    }

    public IResponseTool verifyStudentIsAuthenticated(Map<String, String> parameters) {
        try {
            if (Objects.isNull(acuId)) getPersonId();
            return new ResponseTool("O usuário está autenticado", "O usuário está autenticado");
        } catch (Exception e) {
            return new ResponseTool("O usuário não está autenticado", "O usuário não está autenticado");
        }
    }

    public IResponseTool getContactUeg(Map<String, String> parameters) {
        return new ResponseTool("", """
                Contatos institucionais da UEG:
                
                 Número geral: (62) 3328-1433
                
                ampus Central:
                 Endereço: Rodovia BR-153, Quadra Área, Km 99, Fazenda Barreiro do Meio, Anápolis/GO, CEP: 75132-400
                
                Secretaria Acadêmica Central:
                 Responsável: Brandina Fátima Mendonça de Castro Andrade
                 Telefone: (62) 3328-1402 / (62) 3328-1152 Ramais 9716 e 9715
                 E-mail: gsec.central@ueg.br
                 SEI: 20259
                
                Coordenação de Diplomas:
                 Responsável: Jane Aparecida Borges Arantes
                 Telefone: (62) 3328-1152
                 E-mail: diploma.prg@ueg.br
                 SEI: 16128
                
                Coordenação de Gestão das Secretarias Acadêmicas:
                 Responsável: Lílian Lopes Fernandes
                 Telefone: (62) 3328-1135
                 E-mail: gsec.central@ueg.br
                 SEI: 20261
                
                Secretaria do Campus Central:
                 E-mail: secretaria.campuscentral@ueg.br
                """);
    }

    public IResponseTool getAboutUeg(Map<String, String> parameters) {
        return new ResponseTool("", """
                A Universidade Estadual de Goiás (UEG) é uma universidade pública multicampi do Estado de Goiás, criada pela Lei Estadual 13.456, de 16 de abril de 1999.
                Nos termos do seu Estatuto, aprovado pelo Decreto Estadual nº 9.593, de 17 de janeiro de 2020 e do Regimento Geral aprovado por seu Conselho Universitário, a UEG é uma instituição de ensino, pesquisa e extensão com finalidade científica e tecnológica, de natureza cultural e educacional, com caráter público, gratuito e laico. Trata-se de uma autarquia do poder executivo do Estado de Goiás, com autonomia didático-científica, administrativa e de gestão financeira e patrimonial, nos termos do Artigo 207 da Constituição da República Federativa do Brasil, do Artigo 161 da Constituição do Estado de Goiás e da Lei Estadual nº 18.971, de 23 de julho de 2015. Rege-se por seu Estatuto, seu Regimento Geral e por suas normas complementares.
                A UEG possui sede no município de Anápolis (GO) e alcance acadêmico organizado em oito regiões do estado, a partir de Câmpus e Unidades Universitárias (UnU) presenciais, assim como de Polos de Educação a Distância (EaD). Esta presença alcança todas as microrregiões de Goiás definidas pelo Instituto Brasileiro de Geografia e Estatística (IBGE), atribuindo à UEG, como única universidade pública estadual de Goiás, perfil e função estratégica para a interiorização do acesso, das condições, dos processos e dos resultados da educação superior pública, do desenvolvimento científico e tecnológico e da inovação que ele promove desde o âmbito local nos municípios.
                No limiar da celebração do seu jubileu de prata, estas características alicerçam o perfil da UEG como Instituição Pública Estadual de Educação Superior, Ciência e Tecnologia, dedicada a alcançar e responder, local e regionalmente, às demandas de formação de pessoal de nível superior nos municípios goianos para o seu desenvolvimento.
                """);
    }

    private List<String> getDisciplineNames() {
        List<String> disciplineNames = new ArrayList<>();

        List<IDisciplineGrade> disciplineGrades = this.getGrades();
        for (IDisciplineGrade dg : disciplineGrades) {
            disciplineNames.add(dg.getDisciplineName());
        }
        return disciplineNames;
    }
}
