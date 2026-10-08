package com.example.AminalReport.util;

import org.springframework.security.core.userdetails.UserDetails;

public final class AutenticacaoUtils {

    private AutenticacaoUtils() {
    }

    /**
     * Retorna o e-mail (username) do usuário autenticado, ou {@code null}
     * para visitantes anônimos em rotas públicas.
     */
    public static String emailOuNulo(UserDetails usuario) {
        return usuario != null ? usuario.getUsername() : null;
    }
}
