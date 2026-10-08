package com.example.AminalReport.entities.enums;

public enum EnumTipoOrg {
    ONG("ONG"),
    SECRETARIA_MUNICIPAL("Secretaria Municipal"),
    POLICIA_AMBIENTAL("Polícia Ambiental");

    private final String descricao;

    EnumTipoOrg(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}