# Tarefas da classificação de urgência

**Entrada:** [spec.md](spec.md), [plan.md](plan.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/openapi.yaml](contracts/openapi.yaml)  
**Formato:** `[ID] [P?] [US?] ação com caminho exato e resultado verificável`  
**Status:** rascunho. Nenhuma tarefa foi executada; todas estão abertas. O plano depende de spec ainda não aprovada (ver [checklists/requirements.md](checklists/requirements.md)).

- `[P]` indica que a tarefa pode ser executada em paralelo porque não altera os mesmos arquivos nem depende de outra tarefa pendente.
- `[US1]` relaciona a tarefa a uma história de usuário.
- Marque `[x]` somente quando o resultado e sua verificação estiverem concluídos.
- Caminhos de código abreviados: `main/` = `src/main/java/com/example/AminalReport/`, `test/` = `src/test/java/com/example/AminalReport/`.

## Fase 1 Preparação

- [ ] T001 Adicionar `com.h2database:h2` com escopo `test` em `pom.xml` e criar `src/test/resources/application-test.properties` (H2 em modo PostgreSQL, `ddl-auto=create-drop`, `urgencia.ia.habilitada=false`). Verificação: `./mvnw -q test` executa `DenunciaMapperTest` sem PostgreSQL.
- [ ] T002 [P] Criar `main/config/UrgenciaIaProperties.java` (`@ConfigurationProperties("urgencia")`, registrada na aplicação) e acrescentar as propriedades `urgencia.*` sem segredo em `src/main/resources/application.properties`; criar `.env.example` com `URGENCIA_IA_API_KEY=` vazio. Verificação: teste de binding em `test/config/UrgenciaIaPropertiesTest.java` e app sobe sem a chave.
- [ ] T003 [P] Criar ou atualizar `.gitignore` na raiz para ignorar `.env`. Verificação: `git check-ignore .env` retorna o caminho.

## Fase 2 Base compartilhada

- [ ] T004 [P] Criar `main/entities/enums/EnumOrigemClassificacao.java` com `IA`, `REGRAS`, `FALLBACK`, `USUARIO`. Verificação: compila.
- [ ] T005 Acrescentar a `main/entities/formularios/Denuncia.java` os campos `justificativaUrgencia` (TEXT), `confiancaUrgencia`, `origemClassificacao` (enum, anulável), `versaoClassificador` e `revisaoManual` (`boolean default false`), com getters e setters. Verificação: o app sobe sobre um banco com denúncias existentes e as colunas são criadas (quickstart).
- [ ] T006 [P] Criar `main/service/urgencia/EntradaClassificacao.java`, `ResultadoClassificacao.java` (com validação de nível, justificativa até 300 caracteres e confiança de 0 a 1) e `ClassificadorUrgencia.java` conforme `data-model.md`. Verificação: compila.
- [ ] T007 [P] Criar `test/service/urgencia/ResultadoClassificacaoTest.java` com casos válidos e inválidos (nível nulo, justificativa vazia ou longa, confiança fora do intervalo). Verificação: teste passa depois da T006.
- [ ] T008 Expor justificativa, confiança, origem (nula vira `USUARIO`) e revisão manual em `main/dto/response/DenunciaResponse.java` e `main/mapper/DenunciaMapper.java#toResponse`; cobrir em `test/mapper/DenunciaMapperTest.java`. Depende de T004 e T005. Verificação: teste do mapper passa.

## Fase 3 História de usuário 1 Registrar denúncia sem estimar a urgência (P1)

**Objetivo:** denúncia padrão registrada sem campo de urgência, salva com nível e justificativa e exibida nas telas.  
**Teste independente:** com o app rodando (IA simulada ou real), registrar uma denúncia sem urgência e ver nível e justificativa em home, status e detalhe.

- [ ] T009 [P] [US1] Criar `test/service/urgencia/ClassificadorIaTest.java` com `MockRestServiceServer`: payload contém apenas tipo de animal, descrição e ponto de referência (sem contato, e-mail, foto); resposta válida vira `ResultadoClassificacao` com origem `IA`. Cobre CA-001, CA-002, RF-003.
- [ ] T010 [P] [US1] Criar `test/service/urgencia/ClassificacaoUrgenciaServiceTest.java` com classificador falso: caminho feliz, truncamento da descrição ao limite e resultado sempre presente. Cobre RF-002.
- [ ] T011 [P] [US1] Criar `test/service/DenunciaRegistroUrgenciaTest.java` (`@SpringBootTest`, perfil `test`, classificador falso): `registrar` salva `urgencia`, justificativa, origem e versão e nunca grava nível nulo. Cobre CA-001, CA-002, RF-004.
- [ ] T012 [P] [US1] Criar `test/controller/DenunciaViewsUrgenciaTest.java` (MockMvc): o formulário `/denuncia` não contém `nivelUrgencia`; home e detalhe mostram o texto do nível e a justificativa. Cobre RF-001, RF-010.
- [ ] T013 [P] [US1] Criar `test/service/urgencia/ClassificacaoUrgenciaLogTest.java` com `OutputCaptureExtension`: o log traz resultado, duração e tokens, e não contém descrição, ponto de referência, contato nem chave. Cobre RF-011, RQ-004.
- [ ] T014 [US1] Criar `src/main/resources/prompts/urgencia-v1.txt` (instrução de sistema, rubrica provisória dos cinco níveis até a Q-001, saída em formato fixo, relato declarado como dado) e `main/service/urgencia/PromptUrgenciaBuilder.java` que delimita o texto do usuário. Verificação: teste de unidade do builder em `test/service/urgencia/PromptUrgenciaBuilderTest.java`.
- [ ] T015 [US1] Implementar `main/service/urgencia/ClassificadorIa.java` com `RestClient`, timeout, saída estruturada e validação rígida, lendo `UrgenciaIaProperties`. Verificação: T009 passa.
- [ ] T016 [US1] Implementar a versão mínima de `main/service/urgencia/ClassificacaoUrgenciaService.java`: monta a entrada, trunca, chama a IA e, se falhar, aplica o nível padrão com `FALLBACK` e revisão manual para que o nível nunca seja nulo. Verificação: T010 passa.
- [ ] T017 [US1] Remover `nivelUrgencia` de `main/dto/request/DenunciaRequest.java` e de `DenunciaMapper#toEntity`; ajustar `test/mapper/DenunciaMapperTest.java`. Verificação: compila e o teste do mapper passa.
- [ ] T018 [US1] Chamar a classificação em `main/service/DenunciaService.java#registrar` antes de `save`, copiando nível, justificativa, confiança, origem, versão e revisão manual para a entidade. Verificação: T011 passa.
- [ ] T019 [US1] Remover o `select` "Estimativa de Risco" de `src/main/resources/templates/denuncia.html`. Verificação: T012 passa (parte do formulário).
- [ ] T020 [US1] Exibir a justificativa em `templates/home.html`, `templates/status.html` e `templates/detalhe.html`, mantendo o texto do nível. Verificação: T012 passa (parte das telas).
- [ ] T021 [US1] Registrar o log de cada classificação em `ClassificacaoUrgenciaService` e `ClassificadorIa` (resultado, duração, tokens, tentativas, versão). Verificação: T013 passa.
- [ ] T022 [US1] Executar o teste independente da P1 conforme `quickstart.md` e registrar o resultado em `docs/ia/verificacao-us1.md`.

## Fase 4 História de usuário 2 Não perder a denúncia quando a IA falha (P2)

**Objetivo:** denúncia salva com nível mesmo sem IA ou com relato insuficiente.  
**Teste independente:** com o provedor simulado indisponível, enviar uma denúncia e verificar nível e origem gravados.

- [ ] T023 [P] [US2] Criar `test/service/urgencia/ClassificadorRegrasTest.java`: termos graves, acentos e caixa, ausência de termo (inconclusivo), negação ("não está sangrando") e escolha do maior nível. Cobre RF-005.
- [ ] T024 [P] [US2] Ampliar `ClassificacaoUrgenciaServiceTest.java`: timeout leva a `REGRAS` (CA-003), resposta inválida leva a `REGRAS` (CA-006), regras inconclusivas levam a `FALLBACK` com revisão manual (CA-004), baixa confiança marca revisão, tentativas não passam do máximo, limite excedido pula a IA. Cobre RF-005, RF-006, RF-009, RF-012.
- [ ] T025 [P] [US2] Criar `test/service/urgencia/LimitadorChamadasTest.java` (janela deslizante, reinício após o período). Cobre RF-012.
- [ ] T026 [US2] Implementar `main/service/urgencia/ClassificadorRegras.java` e a tabela `src/main/resources/urgencia/regras-v1.txt` (termos normalizados por nível). Verificação: T023 passa.
- [ ] T027 [US2] Implementar `main/service/urgencia/LimitadorChamadas.java`. Verificação: T025 passa.
- [ ] T028 [US2] Completar `ClassificacaoUrgenciaService` com a cadeia IA → regras → padrão, tentativas limitadas, validação rígida, limiar de baixa confiança e marca de revisão manual. Depende de T026 e T027. Verificação: T024 passa.
- [ ] T029 [US2] Exibir a indicação de revisão manual em `templates/home.html`, `templates/status.html` e `templates/detalhe.html` e cobrir em `DenunciaViewsUrgenciaTest.java`. Verificação: teste passa.
- [ ] T030 [US2] Executar CA-003, CA-004 e CA-006 com o provedor simulado e registrar em `docs/ia/verificacao-us2.md`.

## Fase 5 História de usuário 3 Impedir a manipulação do nível (P2)

**Objetivo:** o nível depende do relato, não de instruções embutidas nem de valores forjados.  
**Teste independente:** enviar descrição com instrução embutida e requisição com `nivelUrgencia` forjado.

- [ ] T031 [P] [US3] Criar `test/controller/DenunciaForjaUrgenciaTest.java` (`@SpringBootTest`, MockMvc, classificador falso): `POST /denuncia` com `nivelUrgencia=NAO_URGENTE` salva o nível calculado. Cobre CA-007, RF-008.
- [ ] T032 [P] [US3] Ampliar `PromptUrgenciaBuilderTest.java`: texto com delimitador falso, instrução de ignorar regras e texto longo ficam dentro do bloco de dados; resposta com campos extras é aceita só pelos campos conhecidos. Cobre RF-009.
- [ ] T033 [US3] Endurecer `PromptUrgenciaBuilder` (escape ou remoção de sequências que imitem o delimitador) e o parser de `ClassificadorIa` (ignora campos extras). Verificação: T032 passa.
- [ ] T034 [US3] Executar o caso adversarial de injeção (CA-005) contra o modelo real, se houver chave, e registrar em `docs/ia/verificacao-us3.md`. Sem chave, registrar a limitação.

## Fase 6 História de usuário 4 Reclassificar ao editar o relato (P3)

**Objetivo:** a urgência acompanha mudanças na descrição ou no ponto de referência.  
**Teste independente:** classificar como `POUCO_URGENTE`, editar para um animal ferido e ver o novo nível.

- [ ] T035 [P] [US4] Criar `test/service/DenunciaReclassificacaoTest.java`: edição de descrição reclassifica (CA-008); edição só da foto mantém nível e justificativa; `nivelUrgencia` forjado em `/status/editar/{id}` é ignorado. Cobre RF-007, RF-008.
- [ ] T036 [US4] Remover `nivelUrgencia` de `main/dto/request/DenunciaAlteracaoRequest.java` e de `DenunciaMapper#atualizarEntidade`; ajustar `DenunciaMapperTest`. Verificação: compila.
- [ ] T037 [US4] Reclassificar em `DenunciaService#atualizar` somente se `descricao` ou `pontoRef` mudarem de valor, com tratamento de falha conforme a resposta à Q-012. Verificação: T035 passa.
- [ ] T038 [US4] Remover o `select` de `templates/alterarDenuncia.html` e o atributo `nivelUrgencias` de `main/controller/StatusController.java#formularioEdicao`. Verificação: o formulário de edição abre sem o campo.
- [ ] T039 [US4] Executar o cenário CA-008 no app e registrar em `docs/ia/verificacao-us4.md`.

## Fase 7 História de usuário 5 Avaliar a capacidade da IA (P2)

**Objetivo:** relatório que compara a IA com as regras e informa erros graves, latência e custo.  
**Teste independente:** executar a avaliação sem subir o app web.

- [ ] T040 [P] [US5] Criar `src/test/resources/avaliacao/casos-urgencia.json` a partir de [eval-casos.md](eval-casos.md), depois que o PO validar o gabarito (Q-001). Pelo menos 15 casos, 5 adversariais, parte reservada. Verificação: teste que lê o arquivo e confere as contagens.
- [ ] T041 [P] [US5] Criar `test/avaliacao/MetricasAvaliacaoTest.java` com matriz de confusão conhecida: precisão e recall por nível, erro ordinal e recall de `MUITO_URGENTE` e `EMERGENCIA`.
- [ ] T042 [US5] Implementar `test/avaliacao/MetricasAvaliacao.java`. Verificação: T041 passa.
- [ ] T043 [US5] Criar `test/avaliacao/AvaliacaoUrgenciaRunner.java` com `@Tag("avaliacao")` (excluída da suíte padrão no `pom.xml`): roda regras e IA, mede latência, tokens e custo, repete cinco vezes os casos críticos e grava o relatório.
- [ ] T044 [US5] Executar a avaliação e registrar em `docs/ia/avaliacao-urgencia.md`: métricas por nível, erros graves, comparação com regras e com o nível informado pelo usuário (quando houver exemplo), latência, custo e taxa de fallback, comparados às metas da Q-007.

## Fase final Qualidade transversal

- [ ] T045 Executar `./mvnw test` completo e relacionar cada critério de aceitação (CA-001 a CA-009) a uma evidência.
- [ ] T046 [P] Revisar logs, segredos, acessibilidade (nível em texto, justificativa legível) e mensagens de erro; confirmar que nenhuma chave aparece no repositório e registrar a observação sobre a senha em `application.properties`.
- [ ] T047 [P] Criar `docs/ia/adr-001-modelo-ia.md` (provedor, modelo, versão, parâmetros, custo, retenção de dados e justificativa de usar IA em vez de só regras), `docs/ia/adr-002-fallback.md`, `docs/ia/prompts.md` (versões do prompt) e `docs/ia/custos.md`.
- [ ] T048 [P] Criar ou atualizar `README.md` na raiz para descrever a urgência como classificada pelo sistema, a configuração e os limites.
- [ ] T049 Atualizar `specs/001-classificacao-urgencia/quickstart.md` com os comandos realmente verificados e remover o aviso de "não executado".
- [ ] T050 Conferir [traceability.md](traceability.md) e convergir: comparar implementação, testes e documentos com spec, plano e tarefas; registrar lacunas aceitas.

## Dependências

- T001 bloqueia todos os testes de integração (T011, T012, T031, T035).
- T004, T005 e T006 bloqueiam T008 e toda a Fase 3.
- T014 bloqueia T015; T015 e T016 bloqueiam T018.
- T017 e T018 devem ser concluídas juntas para o projeto voltar a compilar (a remoção do campo quebra o uso em `DenunciaMapper` e no teste).
- US2 depende de T016; US3 depende de T014 e T015; US4 depende de T018 e T028; US5 depende de T028 e das respostas Q-001 e Q-007.
- US1 pode ser implementada e demonstrada antes de US2 a US5.

## Estratégia de incremento

1. Concluir a preparação e a base compartilhada.
2. Entregar a P1 (US1) com teste independente e nível sempre presente.
3. Adicionar US2 (falhas) antes de qualquer uso real com IA, depois US3, US4 e US5 sem quebrar a P1.
4. Executar a verificação final e atualizar os artefatos afetados.
