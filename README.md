# Assistente do Discente - Projeto de Plataforma Digital para Instituições de Ensino

- [Introdução](#introdução)
- [Expansão Futura](#expansão-futura)
- [Sobre o Repositório](#sobre-o-repositório)
- [Funcionamento](#funcionamento)
- [Documentação](#documentação)

---

## Introdução

O projeto propõe a criação de uma **plataforma digital** voltada inicialmente para os alunos de instituições de ensino, com o objetivo de facilitar o acesso às informações acadêmicas de maneira rápida e eficiente. A plataforma foi projetada para permitir consultas através de comandos. Algumas das funcionalidades incluem:

- Consulta de **horários de aulas**
- Consulta de **notas**
- Verificação de **faltas**

## Expansão Futura

Este é o primeiro passo de uma iniciativa maior, que visa, no futuro, expandir a plataforma para atender:

- **Professores**
- **Coordenação**
- **Alta gestão**

A ideia é criar um **ecossistema integrado** que simplifique o gerenciamento e o acesso às informações educacionais para todos os membros da comunidade acadêmica.

---

## Sobre o Repositório

Este repositório contém três principais pacotes:

- **institutionplugin**: Onde estão presentes os plugins das instituições de ensino, atualmente contando com o plugin da UEG (Universidade Estadual de Goiás).
- **integrator**: Contém as classes e interfaces que devem ser implementadas e conhecidas pelos plugins das instituições para se integrarem corretamente com o sistema.
- **main**: Contém a API que se comunicará com os plugins de instituições de ensino para consultar, formatar e entregar ao usuário suas informações acadêmicas.

O sistema foi projetado para ser usado por diferentes universidades, utilizando reflexão para abstrair e atender de maneira flexível as instituições integradas.  
Para o desenvolvimento dos plugins das instituições, o sistema conta com um pacote integrador, que possui as interfaces e classes necessárias para que qualquer instituição possa criar seu próprio plugin e se integrar ao sistema. O método de busca de dados e conexão com o sistema da instituição é de responsabilidade da instituição, que pode decidir a melhor forma de integração com seu ecossistema.

Não é armazenada nenhuma informação dos usuários, exceto os dados usados para comunicação com a instituição, utilizando o método de armazenamento de informações por chave e valor, o que torna mais flexível para a instituição decidir a forma de conexão que utilizará.

---

## Funcionamento

Nesta primeira etapa do projeto, foi desenvolvida uma Skill para Alexa que se integra à API.  
Veja mais em: [Repositório da Skill](https://github.com/Assistente-do-Discente/AD_AlexaHandler)

Para utilizar as funcionalidades do sistema, é necessário que o usuário tenha feito o login no sistema de sua instituição de ensino. Esse processo de login é também o momento de cadastro do usuário no sistema, onde, após um login bem-sucedido e confirmado pela instituição, é gerado um token JWT pelo Servidor de Autorização desenvolvido para este projeto.  
Veja mais em: [Repositório do Servidor de Autorização](https://github.com/Assistente-do-Discente/AD_AuthServer)

Em toda chamada à API, é realizada uma verificação do token presente na requisição. Após a validação do token, ele é utilizado para identificar o usuário e realizar a conexão com sua instituição através do plugin.

---

## Documentação

Os diagramas produzidos para o desenvolvimento do sistema estão no seguinte repositório: [Documentação](https://github.com/Assistente-do-Discente/Documentacao)

--- 
