package com.example.AminalReport.entities.enums;

public enum EnumNivelUrgencia {

    NAO_URGENTE("Não Urgente"),      // Azul
    POUCO_URGENTE("Pouco Urgente"),  // Verde
    URGENTE("Urgente"),              // Amarelo
    MUITO_URGENTE("Muito Urgente"),  // Laranja
    EMERGENCIA("Emergência");        // Vermelho

    private final String descricao;

    EnumNivelUrgencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}