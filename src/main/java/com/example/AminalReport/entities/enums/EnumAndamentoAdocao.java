package com.example.AminalReport.entities.enums;

public enum EnumAndamentoAdocao {
    AGUARDANDO("Aguardando"),
    CONCLUIDA  ("Concluída"),
    RETIRADA("Retirada");

    private final String descricao;

    EnumAndamentoAdocao(String descricao) { this.descricao = descricao;}

    public String getDescricao() {
        return descricao;
    }
}
