# Modelo de dados da classificação de urgência

**Funcionalidade:** classificação automática de urgência da denúncia  
**Spec:** [spec.md](spec.md)

Descreve entidades e regras do domínio. Os nomes de campo seguem o padrão em português do projeto.

## Entidade Denúncia (existente, ampliada)

**Finalidade:** relato de animal em situação de risco. Classe `Denuncia`, que estende `Formulario` (herança `JOINED`). Só os campos relevantes aparecem abaixo.

| Campo | Significado | Obrigatório | Regras |
|---|---|---:|---|
| `id` | Identificador (em `Formulario`) | sim | gerado pelo banco |
| `tipoAnimal` | Tipo do animal (`EnumTipoAnimal`) | sim | entrada da classificação |
| `descricao` | Relato livre | sim no formulário (Q-011); anulável no banco | entrada da classificação; truncada ao limite antes do envio |
| `pontoRef` | Ponto de referência | não | entrada da classificação |
| `contato`, `usuarioCriador`, `fotoPath`, `cep`, `rua`, `bairro`, `municipio`, `estado` | Dados de contato, autoria, foto e endereço | variável | **nunca** enviados ao provedor de IA |
| `urgencia` | Nível atribuído (`EnumNivelUrgencia`) | sim | nunca nula; definida pelo sistema; não aceita valor vindo do cliente |
| `origemClassificacao` | Como o nível foi definido (`EnumOrigemClassificacao`) | não (nova) | nula em denúncias antigas, tratada como `USUARIO` nas respostas |
| `justificativaUrgencia` | Explicação curta do nível (`TEXT`) | não (nova) | até 300 caracteres; vazia só quando a origem é `USUARIO` ou nula |
| `confiancaUrgencia` | Confiança do classificador de 0 a 1 | não (nova) | entre 0 e 1; proposta: `REGRAS` grava 0,5 e `FALLBACK` grava 0,0 (valores fixos, a confirmar) |
| `versaoClassificador` | Modelo e versão do prompt ou da tabela de regras | não (nova) | exemplo: `<modelo>\|prompt-v1`, `regras-v1` |
| `revisaoManual` | Marca de que uma pessoa deve revisar o nível | sim, valor padrão falso (nova) | verdadeira quando a origem é `FALLBACK`, quando a confiança fica abaixo do limiar configurado ou quando o relato é insuficiente |

Os demais campos existentes (`organizacaoResponsavel`, `andamentoDenuncia`) não mudam.

## Enum EnumOrigemClassificacao (novo)

| Valor | Quando |
|---|---|
| `IA` | o agente respondeu e a resposta foi validada |
| `REGRAS` | a IA falhou, estourou o tempo, estava desabilitada, ou o limite de chamadas foi atingido, e o classificador por regras foi conclusivo |
| `FALLBACK` | IA e regras não foram conclusivas; aplicado o nível padrão |
| `USUARIO` | nível informado pelo usuário em denúncias antigas (valor lógico, aplicado na leitura quando a coluna está nula) |

`EnumNivelUrgencia` não muda (cinco valores).

## Valores transitórios (não persistidos)

- **EntradaClassificacao:** `tipoAnimal`, `descricao`, `pontoRef`. Sem outros campos.
- **ResultadoClassificacao:** `nivel`, `justificativa`, `confianca`, `origem`, `versao`, `revisaoManual`.
- **Caso de avaliação:** `id`, `tipoAnimal`, `descricao`, `pontoRef`, `nivelReferencia`, `tipo` (normal ou adversarial), `reservado` (avaliação final). Fica em `src/test/resources/avaliacao/casos-urgencia.json`.

## Relacionamentos

- Uma Denúncia possui no máximo uma classificação vigente, gravada em seus próprios campos.
- Não há tabela de histórico de classificações nesta versão (DTEC 006).

## Estados e transições

```text
(sem nível) -> classificada[IA | REGRAS | FALLBACK] -> reclassificada[IA | REGRAS | FALLBACK]
denúncia antiga: (origem nula = USUARIO) permanece assim até ser editada em descrição ou ponto de referência
/urgente: urgencia = EMERGENCIA fixa; origem em branco (ver Q-013)
```

- A primeira classificação ocorre ao registrar a denúncia padrão, antes de salvar.
- A reclassificação ocorre quando `descricao` ou `pontoRef` mudam de valor na edição. Alterar só a foto não reclassifica.
- Uma denúncia antiga reclassificada passa a ter origem `IA`, `REGRAS` ou `FALLBACK`.
- A transição é rejeitada (valor do cliente ignorado) quando a requisição traz `nivelUrgencia`.
- Se todas as etapas falharem, o resultado é o nível padrão com `FALLBACK`; o estado "sem nível" nunca é gravado.

## Validações transversais

- `urgencia` nunca é nula depois de `registrar` ou `atualizar` (a coluna é `nullable = false` e os templates assumem valor).
- Saída do agente: `nivel` precisa existir em `EnumNivelUrgencia`; `confianca` entre 0 e 1; `justificativa` não vazia. Qualquer violação descarta a resposta inteira.
- Linhas já existentes não podem violar restrição nova: colunas anuláveis, e `revisaoManual` com valor padrão falso.
- A justificativa não deve conter contato nem dado pessoal; a instrução ao agente o proíbe e o log nunca a registra.
