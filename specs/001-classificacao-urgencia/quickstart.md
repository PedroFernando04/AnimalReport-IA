# Guia de validação local da classificação de urgência

**Funcionalidade:** [spec.md](spec.md)

Este guia descreve como outra pessoa prepara o ambiente e verifica o incremento.

> **Estado:** os comandos abaixo são **propostos e ainda não foram executados**, pois a implementação não existe. Devem ser verificados na T049 antes de a entrega ser considerada completa.

## Pré requisitos

- Java 21
- PostgreSQL local com o banco `animalreportdb` (conforme `src/main/resources/application.properties`)
- Para testar a IA real: uma chave do provedor escolhido (Q-008) na variável `URGENCIA_IA_API_KEY`. Sem chave, o app roda só com regras e nível padrão.
- Nenhuma credencial é colocada em arquivo versionado.

## Configuração

```bash
cp .env.example .env        # preencha apenas URGENCIA_IA_API_KEY no ambiente local
export URGENCIA_IA_API_KEY="..."   # opcional; sem ela a IA fica desabilitada
```

Propriedades relevantes (valores padrão propostos): `urgencia.ia.habilitada`, `urgencia.ia.modelo`, `urgencia.ia.timeout-ms=4000`, `urgencia.ia.max-tentativas=2`, `urgencia.ia.max-chamadas-por-minuto=30`, `urgencia.confianca-minima=0.6`.

## Execução

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. Colunas novas são criadas por `ddl-auto=update`; denúncias existentes continuam abrindo.

## Testes

```bash
./mvnw test                      # suíte padrão, sem chamar o provedor real
./mvnw test -Dgroups=avaliacao   # conjunto de avaliação (usa a IA se houver chave)
```

## Verificação da história P1

1. Abra `http://localhost:8080/denuncia`. O formulário **não** deve ter o campo "Estimativa de Risco".
2. Envie: tipo Cachorro, descrição "atropelado, sangrando e sem conseguir levantar", estado, município e bairro preenchidos.
3. Entre como organização (ou use a home pública) e abra a lista e o detalhe da denúncia.
4. Esperado: a denúncia aparece com um nível em texto (alto) e uma justificativa; no banco, `origem_classificacao` é `IA` (com chave) ou `REGRAS` (sem chave).
5. Repita com "gato solto na rua, aparentemente saudável, quero saber se há abrigo": nível `NAO_URGENTE` ou `POUCO_URGENTE`.
6. Verificação de forja: `curl -F tipoAnimal=GATO -F descricao="teste" -F estado=SP -F municipio=X -F bairro=Y -F nivelUrgencia=NAO_URGENTE http://localhost:8080/denuncia` e confirme que o nível salvo é o calculado.

Evidência a registrar: captura ou consulta SQL do registro, em `docs/ia/verificacao-us1.md` (T022).

## Verificação das falhas (US2)

1. Inicie o app sem `URGENCIA_IA_API_KEY` ou com um valor inválido e envie uma denúncia com "atropelado, sangrando": origem `REGRAS`, sem erro na tela.
2. Envie a descrição "ajuda": nível padrão, `revisao_manual = true`, justificativa informando falta de informações.

## Falhas conhecidas

- O nível padrão e o limite de custo dependem de Q-002 e Q-008.
- Reclassificação com falha na edição segue a proposta de Q-012 até o PO decidir.
- O limite de chamadas é por instância e zera ao reiniciar.
- Denúncias de `/urgente` e as antigas não têm justificativa.
