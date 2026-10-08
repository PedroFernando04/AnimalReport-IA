package com.example.AminalReport.dto.response;

import com.example.AminalReport.entities.enums.EnumTipoOrg;

/**
 * Dados do usuário autenticado expostos às views (navbar, perfil).
 * Nunca inclui a senha. Campos específicos ({@code cpfFormatado}, {@code cnpjFormatado},
 * {@code inscricaoEstadual}, {@code tipoOrg}) ficam {@code null} quando não se aplicam.
 *
 * @param tipoUsuario {@code "Comum"} ou {@code "Organizacao"}
 * @param fotoUrl     URL pronta para uso em {@code <img>}, já com avatar padrão
 */
public record UsuarioLogadoResponse(
        Long id,
        String nome,
        String email,
        String telefoneFormatado,
        String foto,
        String fotoUrl,
        String tipoUsuario,
        String cpfFormatado,
        String cnpjFormatado,
        String inscricaoEstadual,
        EnumTipoOrg tipoOrg) {
}
