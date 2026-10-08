package com.example.AminalReport.exception;

/**
 * Violação de uma regra de negócio (ex.: e-mail já cadastrado).
 * A mensagem é segura para ser exibida ao usuário.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
