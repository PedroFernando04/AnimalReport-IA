# Rastreabilidade da classificação de urgência

Esta matriz liga requisito, cenário, teste planejado e tarefa. Os nomes de teste são planejados e passam a ser links quando a implementação existir. Os cenários usam a numeração de [spec.md](spec.md); entre parênteses, o CA do SPEC-01.

| Requisito | Cenário | Teste planejado | Tarefas |
|---|---|---|---|
| RF-001 | US1 cenário 3; formulário sem o campo | `DenunciaViewsUrgenciaTest` | T012 T017 T019 |
| RF-002 | US1 cenários 1 e 2 (CA-001, CA-002) | `ClassificadorIaTest`, `ClassificacaoUrgenciaServiceTest`, `DenunciaRegistroUrgenciaTest` | T009 T010 T011 T015 T016 T018 |
| RF-003 | US1 cenário 1; RQ-003 | `ClassificadorIaTest` (payload mínimo) | T009 T015 |
| RF-004 | US1 cenários 1 e 2; RQ-006 | `DenunciaRegistroUrgenciaTest`, `DenunciaMapperTest` | T005 T008 T011 T018 |
| RF-005 | US2 cenários 1 e 3 (CA-003, CA-006) | `ClassificacaoUrgenciaServiceTest`, `ClassificadorRegrasTest` | T023 T024 T026 T028 |
| RF-006 | US2 cenário 2 (CA-004) | `ClassificacaoUrgenciaServiceTest` | T016 T024 T028 |
| RF-007 | US4 cenários 1 e 2 (CA-008) | `DenunciaReclassificacaoTest` | T035 T036 T037 T038 |
| RF-008 | US3 cenário 2 (CA-007); US4 cenário 3 | `DenunciaForjaUrgenciaTest`, `DenunciaReclassificacaoTest` | T017 T031 T035 T036 |
| RF-009 | US3 cenário 1 (CA-005); US2 cenário 3 (CA-006) | `PromptUrgenciaBuilderTest`, `ResultadoClassificacaoTest`, `ClassificacaoUrgenciaServiceTest` | T006 T007 T014 T024 T032 T033 |
| RF-010 | US1 cenário 3 | `DenunciaViewsUrgenciaTest` | T012 T020 T029 |
| RF-011 | RQ-004 | `ClassificacaoUrgenciaLogTest` | T013 T021 |
| RF-012 | US2 cenário 4 | `LimitadorChamadasTest`, `ClassificacaoUrgenciaServiceTest` | T002 T024 T025 T027 T028 |
| CS-001 a CS-005, RQ-001, RQ-008 | US5 cenário 1 (CA-009) | `AvaliacaoUrgenciaRunner`, `MetricasAvaliacaoTest` | T040 T041 T042 T043 T044 |
| RQ-002 | revisão de segredos | revisão T046; teste de binding | T002 T003 T046 |
| RQ-007 | revisão de acessibilidade | `DenunciaViewsUrgenciaTest`; revisão T046 | T012 T020 T046 |

## Cobertura das histórias

| História | Tarefas | Teste independente |
|---|---|---|
| US1 (P1) | T009 a T022 | T022 |
| US2 (P2) | T023 a T030 | T030 |
| US3 (P2) | T031 a T034 | T034 |
| US4 (P3) | T035 a T039 | T039 |
| US5 (P2) | T040 a T044 | T044 |

## Lacunas aceitas antes da implementação

- O gabarito dos casos de avaliação é uma proposta; o PO precisa validá-lo (Q-001) antes de T040.
- Metas numéricas de CS-001 a CS-004 e RQ-001 aguardam Q-007 e Q-008; hoje são comparações relativas ou propostas.
- O comportamento em falha na edição (Q-012) e a origem do `/urgente` (Q-013) seguem a proposta da spec até a decisão do PO.
- Não há tarefa para o item "ordenar a lista por urgência", fora do escopo.
- RF-001 e RF-007 não têm teste de interface no navegador; são verificados por MockMvc e pelo quickstart.
