package com.example.AminalReport.entities.enums;

public enum EnumAndamentoDenuncia {
    AGUARDANDO("Aguardando"),
    EM_ANALISE ("Em Análise"),
    CONCLUIDA  ("Concluída"),
    REPROVADA("Reprovada");

    private final String descricao;

    EnumAndamentoDenuncia(String descricao) { this.descricao = descricao;}

    public String getDescricao() {
        return descricao;
    }
}
