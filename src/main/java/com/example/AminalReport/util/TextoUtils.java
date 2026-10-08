package com.example.AminalReport.util;

/**
 * Pequenas rotinas de normalização de texto vindas de formulários HTML.
 */
public final class TextoUtils {

    private TextoUtils() {
    }

    /**
     * Formulários HTML enviam campos não preenchidos como string vazia.
     * Este método converte vazio/em branco para {@code null}, permitindo tratar
     * "campo não informado" de forma uniforme (útil em atualizações parciais).
     */
    public static String vazioParaNulo(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }

    /** Remove tudo que não for dígito (ex.: máscara de CPF, CNPJ, telefone). */
    public static String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}
