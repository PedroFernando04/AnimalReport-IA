# Plano de implementação da classificação de urgência

**Funcionalidade:** classificação automática de urgência da denúncia  
**Spec:** [spec.md](spec.md)  
**Data:** 8 de outubro de 2026  
**Responsáveis:** Gabriel Salles, Paulo Tavares, Pedro Fernando, Victoria Café  
**Status:** rascunho. A spec ainda não foi aprovada (ver [checklists/requirements.md](checklists/requirements.md)); itens que dependem de Q-001 a Q-014 estão marcados como proposta.

## Resumo técnico

O `DenunciaService` passa a chamar um orquestrador de classificação antes de salvar. O orquestrador monta uma entrada mínima (tipo de animal, descrição, ponto de referência), chama o agente de IA com timeout, valida rigidamente a resposta e, em caso de falha, cai para um classificador por regras e depois para um nível padrão com revisão manual. Agente e regras implementam a mesma interface, de modo que o controller e o service não conhecem o provedor. O nível, a justificativa, a confiança, a origem e a versão do prompt são gravados na própria `Denuncia`. O campo de urgência sai dos formulários e dos DTOs de entrada, o que também fecha a possibilidade de forjar o valor. O maior risco, subestimar um caso grave, é tratado por regras que escolhem o nível mais alto entre hipóteses plausíveis, por revisão manual em baixa confiança e por métricas separadas de recall para `MUITO_URGENTE` e `EMERGENCIA`.

## Diferenças entre o SPEC-01 (anexo A) e o código desta branch

Esta branch é mais refatorada do que o anexo A descreve. O plano usa o código real:

| SPEC-01 diz | Código da branch |
|---|---|
| `DenunciaService.saveDenuncia` | `DenunciaService.registrar(DenunciaRequest, email)` e `registrarUrgente(...)` |
| `POST /denuncia` com `@RequestParam` obrigatórios | DTO `DenunciaRequest` (record) com Bean Validation; `descricao` é `@NotBlank`, `pontoRef` é opcional |
| `editarDenuncia` salva o objeto recebido e pode perder campos | `DenunciaService.atualizar` busca a entidade e aplica só os campos informados via `DenunciaMapper.atualizarEntidade`; o problema não existe nesta branch |
| Apenas `AminalReportApplicationTests` | Existem testes de mapper, upload e exception handler; `DenunciaMapperTest` afirma que `nivelUrgencia` é mapeado e precisará mudar |
| README descreve urgência como informada pelo usuário | Não há README na raiz do pacote analisado (confirmar no repositório) |

## Contexto técnico

- **Linguagem e versão:** Java 21, Spring Boot 3.5.7 (Spring Framework 6.2), Maven (`mvnw`).
- **Dependências principais:** nenhuma nova em produção (`RestClient` já vem em `spring-boot-starter-web`). Em teste: `com.h2database:h2` (escopo `test`).
- **Persistência:** PostgreSQL com `spring.jpa.hibernate.ddl-auto=update`; herança `JOINED` (`Formulario` → `Denuncia`).
- **Testes:** JUnit 5, Spring Boot Test, `MockRestServiceServer` para o provedor, H2 no perfil `test`, `OutputCaptureExtension` para logs.
- **Plataforma de execução:** JVM 21, servidor único na porta 8080, navegador atual.
- **Limites de desempenho ou custo (propostas, ver Q-008 e Q-009):** timeout de 4 s por chamada, uma nova tentativa, pior caso 10 s; 30 chamadas por minuto por instância; descrição limitada a 2000 caracteres.
- **Escala prevista:** baixa (uso acadêmico); um servidor.

## Verificação da constituição

| Princípio | Como o plano atende | Evidência prevista |
|---|---|---|
| I Evidência antes de afirmação | Justificativa obrigatória, gerada só do relato; sem informação, o sistema declara a falta e marca revisão | Testes de `ResultadoClassificacao` e do caso "ajuda" (CA-004) |
| II Proteção de dados | Entrada mínima; sem contato, usuário, foto ou endereço; chave em variável de ambiente; logs sem texto livre | Teste de payload (T009), teste de logs (T013), revisão T046 |
| III Verificação humana | Marca `revisaoManual` em baixa confiança, fallback e nível padrão; a IA só classifica | Testes de US2; exibição nas telas (T029) |
| IV Qualidade verificável | Teste por critério de aceitação; avaliação com linha de base, latência e custo | Matriz [traceability.md](traceability.md) e `docs/ia/avaliacao-urgencia.md` |
| V Entrega incremental | US1 funciona só com a IA simulada e a garantia de nível nunca nulo; fallbacks entram em US2 | Quickstart, teste independente de cada história |

Não há exceção conhecida aos princípios.

## Arquitetura e fluxo

```text
POST /denuncia (sem nivelUrgencia)
   -> DenunciaController.registrar
   -> DenunciaService.registrar
        -> ClassificacaoUrgenciaService.classificar(EntradaClassificacao)
             |- LimitadorChamadas (RF-012)
             |- ClassificadorIa      -> provedor externo (timeout, tentativas)
             |- ClassificadorRegras  (fallback e linha de base)
             |- nível padrão + revisão manual (último recurso)
             `- log sem dados pessoais (RF-011)
        -> Denuncia.urgencia + campos de auditoria
        -> DenunciaRepository.save
   -> redirect:/
```

**Responsabilidades**

- `ClassificadorUrgencia` (interface): recebe `EntradaClassificacao`, devolve `Optional<ResultadoClassificacao>` (vazio = inconclusivo ou falha). Não conhece HTTP de entrada nem JPA.
- `ClassificadorIa`: monta o prompt com `PromptUrgenciaBuilder`, chama o provedor, converte a resposta em `ResultadoClassificacao`. Lança exceção de falha, nunca devolve valor inválido.
- `ClassificadorRegras`: tabela de termos versionada; determinístico.
- `ClassificacaoUrgenciaService`: único ponto que decide a cadeia IA → regras → padrão, valida, mede tempo e escreve o log. Garante que sempre devolve um resultado.
- `DenunciaService`: monta a entrada mínima a partir da entidade e copia o resultado para a `Denuncia`.
- Templates apenas exibem; nenhuma regra de urgência fica na view.

## Estrutura do repositório

```text
src/main/java/com/example/AminalReport/
├── config/UrgenciaIaProperties.java                 (novo)
├── entities/enums/EnumOrigemClassificacao.java      (novo)
├── entities/formularios/Denuncia.java               (alterado: colunas de auditoria)
├── dto/request/DenunciaRequest.java                 (alterado: remove nivelUrgencia)
├── dto/request/DenunciaAlteracaoRequest.java        (alterado: remove nivelUrgencia)
├── dto/response/DenunciaResponse.java               (alterado: justificativa, confiança, origem, revisão)
├── mapper/DenunciaMapper.java                       (alterado)
├── service/DenunciaService.java                     (alterado: registrar e atualizar)
├── controller/StatusController.java                 (alterado: sem lista de níveis na edição)
└── service/urgencia/                                (novo)
    ├── ClassificadorUrgencia.java
    ├── EntradaClassificacao.java
    ├── ResultadoClassificacao.java
    ├── ClassificadorIa.java
    ├── ClassificadorRegras.java
    ├── ClassificacaoUrgenciaService.java
    ├── LimitadorChamadas.java
    └── PromptUrgenciaBuilder.java
src/main/resources/
├── application.properties                           (alterado: propriedades urgencia.*)
├── prompts/urgencia-v1.txt                          (novo)
├── urgencia/regras-v1.txt                           (novo)
└── templates/{denuncia,alterarDenuncia,home,status,detalhe}.html (alterados)
src/test/java/com/example/AminalReport/
├── mapper/DenunciaMapperTest.java                   (alterado)
├── service/urgencia/*Test.java                      (novos)
├── service/DenunciaRegistroUrgenciaTest.java, DenunciaReclassificacaoTest.java (novos)
├── controller/DenunciaForjaUrgenciaTest.java, DenunciaViewsUrgenciaTest.java (novos)
└── avaliacao/                                       (novo; execução sob demanda)
src/test/resources/
├── application-test.properties                      (novo)
└── avaliacao/casos-urgencia.json                    (novo)
docs/ia/                                             (novo: ADRs, prompts, custos, avaliação)
```

**Decisão de estrutura:** manter o pacote por camadas já usado (`controller`, `service`, `dto`, `mapper`, `entities`) e isolar a nova capacidade em `service/urgencia`, para que a troca de provedor ou o uso apenas das regras não toque no restante. Nenhum módulo ou projeto novo.

## Contratos

- Interface HTTP do app: `POST /denuncia` e `POST /status/editar/{id}` deixam de aceitar `nivelUrgencia` e redirecionam como hoje. `POST /urgente` não muda. Ver [contracts/openapi.yaml](contracts/openapi.yaml).
- Contrato consumido: o agente deve devolver um objeto JSON com `nivel`, `justificativa` e `confianca` (esquema `ResultadoAgente` no mesmo arquivo). O formato de transporte exato depende do provedor (DTEC 001).
- Erros: nenhuma falha de classificação chega ao usuário como erro; o usuário é redirecionado normalmente. Erros de validação do formulário continuam como hoje.
- Versionamento: o prompt e a tabela de regras têm sufixo de versão (`v1`) gravado em `versaoClassificador` (ex.: `claude-…|prompt-v1`).

## Dados

- Novas colunas em `denuncia`: justificativa, confiança, origem, versão do classificador e revisão manual. Detalhes em [data-model.md](data-model.md).
- Origem dos dados: resultado do orquestrador. Validação: nível no enum, justificativa não vazia (até 300 caracteres), confiança entre 0 e 1.
- Retenção: a justificativa pode repetir trechos do relato, por isso segue a retenção da própria denúncia. Nada é enviado nem guardado além do necessário no provedor; a política de retenção do provedor deve constar no ADR (Q-008).
- Denúncias antigas permanecem como estão (Q-010).

## Estratégia de testes

- **Unitários:** validação de `ResultadoClassificacao`; `ClassificadorRegras` (acentos, caixa, termo grave, inconclusivo, negação); `LimitadorChamadas`; `PromptUrgenciaBuilder` (delimitadores e escape); cadeia de fallback do `ClassificacaoUrgenciaService`.
- **Integração:** `ClassificadorIa` contra `MockRestServiceServer` (payload mínimo, resposta válida e inválida, timeout); `DenunciaService.registrar` e `atualizar` com H2 e classificador falso, verificando colunas gravadas.
- **Contrato:** `POST /denuncia` e `/status/editar/{id}` com `nivelUrgencia` forjado (ignorado); formulários sem o `select`.
- **Jornada:** home, status e detalhe mostram nível e justificativa (MockMvc com Thymeleaf).
- **Casos de falha:** IA fora do ar, resposta inválida, limite excedido, descrição sem conteúdo útil, injeção, texto muito longo.
- **Avaliação (CA-009):** 15 ou mais casos em `casos-urgencia.json`, executados sob demanda com a anotação `@Tag("avaliacao")`, excluída da suíte padrão. Roda regras sempre e IA somente com chave configurada.

## Observabilidade e operação

- Log por classificação: resultado (`IA`, `REGRAS`, `FALLBACK`), duração em ms, tokens de entrada e saída (quando o provedor informar), número de tentativas e versão. Sem descrição, ponto de referência, contato, e-mail ou chave.
- Contador simples de classificações por origem (log periódico ou endpoint do Actuator, se o grupo decidir adicioná-lo; **não** está no escopo desta versão).
- Timeout por chamada, no máximo duas tentativas, depois regras; limite de chamadas por minuto, depois regras.
- Chave ausente: a IA fica desabilitada e o sistema opera só com regras e padrão, com aviso no log de inicialização.

## Riscos técnicos

| Risco | Consequência | Mitigação | Como verificar |
|---|---|---|---|
| Subestimar caso grave | Animal deixa de ser priorizado | Regras escolhem o maior nível; baixa confiança gera revisão; recall separado dos níveis altos | Avaliação (CS-002) |
| Truncar a descrição perde o trecho decisivo | Classificação errada em texto longo | Manter início e fim do texto (Q-014) | Caso adversarial de texto longo |
| Injeção de instruções no relato | Nível manipulado | DTEC 008, validação da saída, casos adversariais | CA-005 e avaliação |
| Provedor lento ou indisponível | Formulário demora | Timeout, uma nova tentativa, regras | CA-003 e medição de latência |
| Custo descontrolado (denúncia anônima em `/denuncia` e edição por qualquer usuário autenticado) | Gasto inesperado | `LimitadorChamadas`, teto no provedor, edição só reclassifica se o texto mudou | Teste do limitador e de edição |
| Coluna nova quebra a tabela existente | Aplicação não sobe | Colunas anuláveis ou com valor padrão | Verificação do quickstart em banco com dados |
| Viés por tipo de animal ou linguagem | Animais de produção subestimados | Casos de avaliação com vários tipos de animal; recall por tipo observado | Relatório de avaliação |
| Dado pessoal no relato é enviado ao provedor | Exposição | Aviso na política do app e decisão do PO (Q-008); log sem texto | Revisão T046 e ADR |
| Credencial de banco em texto no `application.properties` (`postgres/123456`) | Exposição ao publicar o repositório | Fora do escopo; recomendar mover para variável de ambiente | Revisão T046 |

## Complexidade e exceções

| Exceção | Por que é necessária | Alternativa mais simples rejeitada porque |
|---|---|---|
| Dependência H2 em teste | Testar persistência e controllers sem PostgreSQL local | Só testes com mocks não verificariam colunas, `nullable` e telas |

Nenhuma exceção aos princípios da constituição.

## Saídas do planejamento

- [research.md](research.md) para as decisões técnicas;
- [data-model.md](data-model.md) para entidades e validações;
- [contracts/openapi.yaml](contracts/openapi.yaml) para as interfaces;
- [quickstart.md](quickstart.md) para validar localmente;
- [tasks.md](tasks.md) criado a partir deste plano e [traceability.md](traceability.md) para a matriz de rastreabilidade;
- [eval-casos.md](eval-casos.md): rascunho do conjunto de avaliação, com gabarito proposto para validação do PO.
