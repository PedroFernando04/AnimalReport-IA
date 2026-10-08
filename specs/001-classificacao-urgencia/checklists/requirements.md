# Checklist de qualidade dos requisitos da classificação de urgência

**Finalidade:** verificar se a especificação está pronta para planejamento  
**Funcionalidade:** [spec.md](../spec.md)  
**Revisor:** rascunho preparado pelo agente; o grupo deve confirmar item a item  
**Data:** 8 de outubro de 2026

Marcar um item significa que o revisor avaliou a qualidade do texto do requisito. Não significa que a implementação está pronta.

## Completude

- [x] CHK001 O usuário, a situação e o resultado esperado estão definidos.
- [x] CHK002 O escopo incluído e o que ficou de fora estão explícitos.
- [x] CHK003 Fluxos alternativos, falhas e casos de borda estão cobertos (US2, US3 e seção de casos de borda).
- [x] CHK004 Entidades relevantes estão definidas sem antecipar a implementação.

## Clareza

- [x] CHK005 Cada requisito descreve comportamento observável.
- [ ] CHK006 Termos vagos possuem medida, condição ou referência. **Pendente:** "nível alto" e "nível baixo" dependem do gabarito (Q-001); "baixa confiança" e "sem informação útil" não têm limiar; "limite combinado" de latência e custo (CS-004) depende de Q-008.
- [ ] CHK007 Questões abertas indicam responsável e prazo. **Pendente:** os prazos estão expressos como marcos ("antes do plano"), sem data; o grupo deve fixar datas com o PO.

## Consistência

- [x] CHK008 Histórias, requisitos e critérios de sucesso não se contradizem. Conflito encontrado entre CA-004 (descrição vazia) e o código (`@NotBlank`) foi registrado como Q-011.
- [x] CHK009 Prioridades refletem o valor e permitem entrega incremental (P1 não depende das demais).
- [x] CHK010 Suposições estão separadas de decisões confirmadas. Nenhuma decisão está confirmada ainda.

## Verificabilidade

- [x] CHK011 Cada história possui teste independente.
- [x] CHK012 Cenários de aceitação definem estado, ação e resultado.
- [ ] CHK013 Requisitos de qualidade podem ser verificados objetivamente. **Pendente:** RQ-001 e RQ-008 são propostas sem confirmação; RQ-005 não tem teto numérico.
- [x] CHK014 Ausência de informação e indisponibilidade possuem comportamento definido (US2).

## Resultado da revisão

**Aprovada para planejamento:** não, por enquanto  
**Pendências bloqueadoras:**

- Q-002 (nível padrão) e Q-011 (descrição vazia): mudam o comportamento de RF-006 e CA-004.
- Q-008 (provedor externo e teto de custo): sem ela o plano não pode escolher provedor, e CS-004 e RQ-005 não são verificáveis.
- Q-001 e Q-007 (gabarito e metas): bloqueiam apenas a US5 e o ajuste do prompt, não a US1.
- Q-009 e Q-014 (síncrono ou assíncrono, truncamento): afetam RQ-001 e a arquitetura.

**Pendências não bloqueadoras:** Q-003, Q-004, Q-005, Q-006, Q-010, Q-012 e Q-013 (têm proposta padrão registrada na spec); datas dos prazos (CHK007).

**Observação:** `plan.md`, `research.md`, `tasks.md` e demais artefatos desta pasta foram escritos como **rascunho** a pedido do grupo, antes desta aprovação. Eles marcam como "proposta" tudo que depende das pendências acima e devem ser revisados quando o PO responder.
