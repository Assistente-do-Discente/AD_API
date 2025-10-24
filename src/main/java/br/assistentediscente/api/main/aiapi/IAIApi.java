package br.assistentediscente.api.main.aiapi;

import br.assistentediscente.api.integrator.institutions.info.IDiscipline;
import br.assistentediscente.api.main.dto.ServiceAndActivationNames;

import java.util.List;

public interface IAIApi {

    String guessDisciplineNameFromIDiscipline(String disciplineIntent, List<? extends IDiscipline> disciplines);

    String startDisciplineNameQuestion = "Assuma que você é um processador que identifica qual disciplina um" +
            " texto está se referindo. As disciplinas que você conhece são: ";

    String endDisciplineNameQuestion = "Responda retornando somente o nome da disciplina por exemplo, retorne" +
            " Programação para dispositivos móveis para programação de celular." +
            " Converta todo número para algarismo romano, como por exemplo: " +
            " retorne Econometria I para Econometria 1 ou Econometria um." +
            " Entenda também quando for fornecido apenas uma sigla, como por exemplo: " +
            "retorne Prática interdisciplinar de aplicações em sistemas de informação I" +
            "para piasi 1 ou PIASI 1. Caso não encontre,retorne somente a mensagem 'NENHUMA' " +
            ".Qual a disciplina para ? ";

    String startItemQuestion = "Assuma que você é um processador que identifica qual item um" +
            " texto está se referindo. Os items que você conhece são: ";

    String endItemQuestion = "Responda retornando somente o nome do item por exemplo, retorne" +
            " Programação para dispositivos móveis para programação de celular." +
            " Converta todo número para algarismo romano, como por exemplo: " +
            " retorne Econometria I para Econometria 1 ou Econometria um." +
            " Entenda também quando for fornecido apenas uma sigla, como por exemplo: " +
            "retorne Prática interdisciplinar de aplicações em sistemas de informação I" +
            "para piasi 1 ou PIASI 1. Caso não encontre, retorne somente a mensagem 'NENHUMA' " +
            ".Qual o item para ? ";

    String guessServicePluginActivationName(String activationName, List<ServiceAndActivationNames> serviceAndActivationNames);

    String guessItemFromItems(String item, List<String> items);

    String startServiceActivationNameQuestion = "Assuma que você é um processador de serviços. Os serviços que você conhece são: ";

    String serviceActivationProgressNameQuestion = " {servico: '{0}', nomes: [{1}]}, ";

    String endServiceActivationNameQuestion = ".A partir disso responda retornando somente o nome do servico," +
            " para qual o nome: '?' seja mais proximo dos nomes dos servicos conhecidos " +
            "Caso não encontre similaridade em nenhum dos nomes dos servicos responda somente 'NENHUM'";

    String weekdayRecognition = """
            RECONHECIMENTO DE DIA DA SEMANA (apelidos e abreviações)
            Procure dentro das informações abaixo os dias da semana, sabendo que o usuário informou o dia:
            """;

    String nameRecognitionOfDiscipline = """
            RECONHECIMENTO DE DISCIPLINAS (apelidos e abreviações)
                - Muitos estudantes usam apelidos/abreviações para disciplinas. Reconheça e mapeie automaticamente para o nome oficial.
                - Regras de normalização ao comparar nomes:
                  - Ignore maiúsculas/minúsculas.
                  - Remova acentos e pontuação.
                  - Aceite numerais arábicos e romanos equivalentes (ex.: "II" ↔ "2").
                - Dicionário mínimo de exemplos (expansível):
                  - "piasi" → "PRÁTICA INTERDISCIPLINAR DE APLICAÇÕES EM SISTEMAS DE INFORMAÇÃO"
                  - "prog web 2", "programação web 2", "pw2" → "PROGRAMAÇÃO WEB II"
                  - "bd2", "banco de dados 2", "banco de dados ii" → "BANCO DE DADOS II"
                - Quando o usuário usar um apelido:
                  - Use o nome oficial na resposta. Se fizer sentido, mencione o apelido reconhecido entre parênteses na primeira ocorrência para confirmar entendimento.
                  - Exemplos de normalização: "Prog Web II", "programacao web 2", "PW2" → "PROGRAMAÇÃO WEB II".
            
            Nome da disciplina informado pelo o usuário foi:""";

    String ruleScheduleGroupingQuestion = """
            AGRUPAMENTO DE HORÁRIOS (regra)
            - Ordene por horário de início.
            - Una blocos consecutivos quando disciplina, professor e sala forem iguais e o início do bloco atual for igual ao término do bloco anterior.
            - Mostre intervalo único com contagem de blocos entre parênteses. Ex.: "19:00 às 20:40 (2 aulas)".
            - Exiba por dia solicitado; se o usuário não especificar o dia, ofereça escolher.
            """;

    String ruleGradesQuestion = """
            FERRAMENTA DE NOTA (regra)
            - Utilize sempre notas de 0 a 100
            - Na resposta json, as disciplinas tem o item 'mat_media' que representa a média final da matérias, mas na lista 'nota_list' é possivel ver a nota dos 2 bimestres a N1 (1º Avaliação ou 1 va) e N2 (2º Avaliação ou 2 va)
            """;
}
