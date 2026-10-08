# Constituição do projeto

**Projeto:** AnimalReport  
**Versão:** 0.1 (rascunho)  
**Aprovada em:** pendente  
**Responsáveis pela aprovação:** Gabriel Salles, Paulo Tavares, Pedro Fernando, Victoria Café e o PO

Este documento registra princípios estáveis do projeto. Uma decisão específica de uma funcionalidade fica na spec, no plano ou em `research.md`.

> Os princípios abaixo foram derivados do SPEC-01 (seções 5 e 8) e do código atual. Nenhum foi aprovado pelo grupo ou pelo PO ainda.

## Princípios

### I Evidência antes de afirmação

Toda decisão automática que afete o atendimento de uma denúncia deve vir com uma justificativa curta, baseada somente no que o denunciante relatou. O sistema não inventa fatos sobre o animal. Quando o relato não sustenta uma conclusão, o sistema declara a falta de informação e encaminha para revisão manual.

### II Proteção de dados

- Dados enviados a serviços externos são o mínimo necessário e nunca incluem contato, identificação do usuário, endereço completo ou foto.
- Segredos (chaves de API, senhas) ficam em variáveis de ambiente, nunca no repositório nem no navegador.
- Logs não registram texto livre do usuário, dados pessoais nem segredos.

### III Verificação humana

- Casos de baixa confiança, relato insuficiente ou falha do serviço de IA são marcados para revisão manual.
- A IA sugere e classifica; não aciona serviços externos, não encaminha denúncias e não notifica ninguém sem decisão humana.
- Mudanças de comportamento do produto exigem confirmação do PO.

### IV Qualidade verificável

- Toda regra de negócio nova tem teste automatizado; todo critério de aceitação tem evidência registrada.
- Funcionalidades que usam IA têm conjunto de avaliação, linha de base sem IA, e métricas de erro, latência e custo registradas.
- Informação essencial não depende apenas de cor (o texto do nível sempre aparece).
- Um item só é marcado como concluído depois da verificação.

### V Entrega incremental

Cada história prioritária pode ser demonstrada e testada sozinha, com o app rodando. Uma falha de componente externo nunca impede o registro de uma denúncia.

## Restrições do projeto

- Stack atual: Java 21, Spring Boot 3.5.7, Thymeleaf, Spring Security, PostgreSQL, Maven.
- Serviços externos de IA exigem documentação de modelo, versão, parâmetros e custo (exigência da disciplina).
- Uso de IA e prompts registrados conforme o processo da disciplina.
- Teto de custo e prazos: a confirmar com o PO (SPEC-01, questão 8).

## Governança

- Alterações neste documento exigem aprovação do grupo e do PO.
- Specs e planos devem registrar como atendem estes princípios.
- Exceções devem explicar motivo, risco, alternativa rejeitada e prazo de revisão.
