package com.example.AminalReport.exception;

/** Falha de I/O ao gravar ou remover arquivos enviados pelos usuários. */
public class ArmazenamentoException extends RuntimeException {

    public ArmazenamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
