package br.assistentediscente.api.institutionplugin.ueg;

public interface UEGEndpoint {
    String VALIDA_LOGIN = "https://www.app.ueg.br/auth/acesso/valida_login";
    String ENTRA_PORTAL_ESTUDANTE = "https://www.app.ueg.br/portal_estudante";
    String PERFIL = "https://www.app.ueg.br/fenix-back-end/perfil/";
    String HORARIO_AULA = "https://www.app.ueg.br/fenix-back-end/horario_aula";
    String DADOS_DISCIPLINAS = "https://www.app.ueg.br/fenix-back-end/percurso_academico_aluno/lista_matriculas_aluno/?acu_id=";
    String DADOS_ACADEMICOS = "https://www.app.ueg.br/fenix-back-end/perfil/dados_academicos";
    String GERAR_NOVA_DECLARACAO_FREQUENCIA = "https://www.app.ueg.br/fenix-back-end/declaracao_frequencia/";

    String GET_JWT_TOKEN = "https://www.app.ueg.br/auth/jwt/get?sistema=gestao_academica";
    String PRE_GERAR_NOVO_HISTORICO_ACADEMICO ="https://www.app.ueg.br/fenix-back-end/historico_academico/?jwt={0}";
    String GERAR_NOVO_HISTORICO_ACADEMICO = "https://www.app.ueg.br/fenix-back-end/historico_academico/nao_integralizado";

    String DADOS_ATV_COMP_EXT = "https://www.app.ueg.br/fenix-back-end/percurso_academico_aluno/evolucao_academica/?acu_id=";

}
