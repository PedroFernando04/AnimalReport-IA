# Pesquisa e decisões técnicas da classificação de urgência

**Funcionalidade:** [spec.md](spec.md)  
**Data:** 8 de outubro de 2026  
**Status:** rascunho; decisões marcadas como provisórias dependem de respostas do PO

Questões de produto ficam na spec. As decisões abaixo foram tomadas a partir do código da branch e do SPEC-01. Nenhuma documentação externa foi consultada nesta etapa (ver "Fontes consultadas").

## Decisão DTEC 001 Forma de chamar o serviço de IA

**Questão:** como o Spring Boot chamará o provedor de IA?  
**Critérios:** sem dependência nova, testável com provedor simulado, troca de provedor sem tocar no controller, chave só em variável de ambiente.  
**Alternativas consideradas:**

1. `RestClient` (já incluído no `spring-boot-starter-web` 3.5.7) com JSON direto. Sem dependência nova; o formato da API fica sob nosso controle e o `MockRestServiceServer` permite testar.
2. SDK oficial do provedor. Menos código de protocolo, mas acopla o projeto a uma biblioteca e adiciona dependência.
3. Spring AI. Abstração pronta, mas é dependência grande para uma única chamada.
4. Modelo local (por exemplo, via servidor local). Evita enviar dados a terceiros, mas exige infraestrutura que o grupo não tem.

**Decisão (provisória):** opção 1, atrás da interface `ClassificadorUrgencia`. Provedor e modelo ficam em propriedades (`urgencia.ia.*`). A escolha final do provedor depende de Q-008.  
**Justificativa:** menor custo de entrada e permite trocar a implementação por regras ou por outro provedor sem alterar o controller (mitigação prevista no SPEC-01).  
**Consequências:** o grupo mantém o código de requisição e de leitura da resposta. A documentação exigida (modelo, versão, parâmetros, custo) vai para `docs/ia/adr-001-modelo-ia.md`.  
**Condição para revisão:** o PO proibir provedor externo (passa para a opção 4) ou o formato do provedor escolhido exigir autenticação ou streaming além do simples.

## Decisão DTEC 002 Formato da saída e validação

**Questão:** como garantir que o agente devolva nível, justificativa e confiança utilizáveis?  
**Critérios:** rejeitar saída fora do enum (RF-009), estabilidade, simplicidade.  
**Alternativas consideradas:**

1. Saída estruturada do provedor (esquema JSON ou ferramenta com parâmetros tipados), mais validação em Java.
2. Pedir JSON em texto livre e fazer o parse.
3. Pedir só o nome do nível em texto.

**Decisão:** opção 1, com validação rígida no Java: `nivel` deve existir em `EnumNivelUrgencia`, `justificativa` não vazia (truncada em 300 caracteres), `confianca` entre 0 e 1. Campos extras são ignorados.  
**Justificativa:** a validação em Java é a defesa final, independentemente do que o modelo devolver.  
**Consequências:** qualquer violação vira falha de classificação e aciona o fallback (CA-006).  
**Condição para revisão:** o provedor escolhido não oferecer saída estruturada. Então cai-se para a opção 2 com testes adicionais de parse.

## Decisão DTEC 003 Classificação síncrona

**Questão:** o usuário espera a classificação no envio ou ela ocorre depois?  
**Critérios:** simplicidade, garantia de que `urgencia` nunca fica nula (a coluna é `nullable = false` e os templates assumem valor), latência percebida.  
**Alternativas consideradas:**

1. Síncrona com timeout e fallback (proposta do SPEC-01).
2. Assíncrona: salvar com nível provisório e classificar em segundo plano. Exige tarefa agendada ou fila, estado intermediário e telas que lidam com "classificando".

**Decisão (provisória):** opção 1.  
**Justificativa:** menor número de estados e nenhuma infraestrutura nova.  
**Consequências:** o usuário pode esperar até o pior caso de RQ-001 (10 s).  
**Condição para revisão:** latência real medida acima do aceitável, ou resposta do PO a Q-009 pedindo assíncrono.

## Decisão DTEC 004 Classificador por regras

**Questão:** qual a forma da linha de base e do fallback?  
**Critérios:** determinístico, explicável, sem treino, funciona sem rede.  
**Alternativas consideradas:**

1. Tabela de termos por nível, com normalização de maiúsculas, acentos e pontuação, e regra "o maior nível encontrado vence".
2. Modelo clássico de aprendizado de máquina. Não há dados rotulados.
3. Nenhum fallback: usar só o nível padrão.

**Decisão:** opção 1, com a tabela em `src/main/resources/urgencia/regras-v1.txt` (versionada). Se nenhum termo for encontrado, o resultado é "inconclusivo" e o orquestrador usa o nível padrão com revisão manual.  
**Justificativa:** simples, auditável e escolhe o nível mais alto entre hipóteses plausíveis, o que reduz o pior erro (subestimar).  
**Consequências:** erra com gíria, grafia e negação ("não está sangrando"). Esses casos entram no conjunto de avaliação.  
**Condição para revisão:** a avaliação mostrar que as regras produzem mais erros graves que o nível padrão.

## Decisão DTEC 005 Resiliência e controle de custo

**Questão:** como implementar timeout, tentativas e limite de chamadas?  
**Critérios:** poucas dependências, comportamento testável, RF-012.  
**Alternativas consideradas:**

1. Código próprio: timeout no `RestClient`, laço de tentativas e janela deslizante em memória (`LimitadorChamadas`).
2. Resilience4j (circuit breaker, rate limiter). Mais completo, porém dependência nova.

**Decisão:** opção 1.  
**Consequências:** o limite vale por instância da aplicação e é zerado ao reiniciar. É suficiente para um servidor único.  
**Condição para revisão:** mais de uma instância da aplicação, ou necessidade de circuit breaker.

## Decisão DTEC 006 Persistência dos novos campos

**Questão:** como acrescentar colunas sem quebrar os dados existentes?  
**Critérios:** `ddl-auto=update` já em uso, ausência de Flyway ou Liquibase no `pom.xml`.  
**Alternativas consideradas:**

1. Colunas novas na tabela `denuncia` (herança `JOINED` a partir de `Formulario`), todas anuláveis ou com valor padrão, deixando o `ddl-auto=update` criá-las.
2. Tabela separada de classificações. Dá histórico, mas não é necessária nesta versão.

**Decisão:** opção 1. A marca de revisão manual usa `columnDefinition = "boolean default false"` (ou tipo `Boolean` anulável tratado como falso), porque uma coluna `NOT NULL` sem valor padrão falha em tabela com linhas existentes.  
**Consequências:** não há histórico de classificações; só a última fica gravada. Denúncias antigas ficam com origem nula (tratada como `USUARIO` nas respostas).  
**Condição para revisão:** necessidade de auditar mudanças de nível (por exemplo, quando houver correção manual pela organização) ou adoção de migração versionada.

## Decisão DTEC 007 Ambiente de testes

**Questão:** como testar persistência e controllers sem um PostgreSQL local?  
**Critérios:** testes rápidos, sem Docker, o `application.properties` atual aponta para PostgreSQL local.  
**Alternativas consideradas:**

1. H2 em memória (escopo de teste) com perfil `test`.
2. Testcontainers com PostgreSQL. Mais fiel, exige Docker.
3. Apenas testes unitários com mocks.

**Decisão:** opção 1, com o modo de compatibilidade PostgreSQL do H2.  
**Consequências:** pode haver diferenças pequenas de dialeto. A verificação manual do quickstart continua sendo feita no PostgreSQL.  
**Condição para revisão:** algum teste falhar por diferença de dialeto.

## Decisão DTEC 008 Defesa contra instruções no relato

**Questão:** como impedir que o texto do usuário altere o comportamento do agente?  
**Critérios:** CA-005, RF-009, custo baixo.  
**Alternativas consideradas:**

1. Instrução de sistema que declara o relato como dado, texto do usuário dentro de delimitadores, remoção ou escape de sequências que imitem o delimitador, e validação da saída (DTEC 002).
2. Segunda chamada de IA para detectar injeção. Mais custo e latência.
3. Piso por regras: se as regras encontrarem termos graves e a IA devolver nível dois ou mais abaixo, usar o maior. Reduz o risco de subestimar, mas é comportamento novo que depende de confirmação do PO.

**Decisão:** opção 1 agora. A opção 3 fica como candidata, a decidir depois de ver os erros da avaliação (US5) e com confirmação do PO.  
**Consequências:** nenhuma defesa de prompt é completa; por isso os casos adversariais são parte obrigatória da avaliação.  
**Condição para revisão:** qualquer caso adversarial do conjunto alterar o nível.

## Fontes consultadas

- SPEC-01 (07/10/2026), seções 5 a 8 e anexos A e B.
- Código da branch `AnimalReport-branchPedro3`: `DenunciaController`, `StatusController`, `DenunciaService`, `DenunciaMapper`, `Denuncia`, `Formulario`, `EnumNivelUrgencia`, DTOs de denúncia, `SecurityConfig`, `application.properties`, `pom.xml` e templates `denuncia`, `alterarDenuncia`, `home`, `status` e `detalhe`.
- **A verificar na implementação (não consultado):** documentação do `RestClient` do Spring Framework 6.2, documentação do provedor escolhido (formato de saída estruturada, preços, política de retenção de dados) e documentação do H2.
