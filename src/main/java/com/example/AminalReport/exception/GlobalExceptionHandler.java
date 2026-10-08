package com.example.AminalReport.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.stream.Collectors;

/**
 * Tratamento centralizado de erros dos formulários: em vez de uma página de
 * erro, o usuário volta para a tela de origem com a mensagem em {@code erro}
 * (exibida pelo fragmento {@code fragments/alerta}).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    static final String ATRIBUTO_ERRO = "erro";

    @ExceptionHandler(RegraDeNegocioException.class)
    public String tratarRegraDeNegocio(RegraDeNegocioException ex,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        return voltarComErro(ex.getMessage(), request, redirectAttributes);
    }

    /** Falhas de Bean Validation e de conversão de tipos nos DTOs de entrada. */
    @ExceptionHandler(BindException.class)
    public String tratarValidacao(BindException ex,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        String mensagem = ex.getAllErrors().stream()
                .map(GlobalExceptionHandler::mensagemDe)
                .distinct()
                .collect(Collectors.joining(" "));
        return voltarComErro(mensagem, request, redirectAttributes);
    }

    private static String mensagemDe(ObjectError erro) {
        if (erro instanceof FieldError campo && campo.isBindingFailure()) {
            return "Valor inválido para o campo '" + campo.getField() + "'.";
        }
        return erro.getDefaultMessage() != null ? erro.getDefaultMessage() : "Dados inválidos.";
    }

    private String voltarComErro(String mensagem,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ATRIBUTO_ERRO, mensagem);
        return "redirect:" + caminhoDeRetorno(request);
    }

    /**
     * Usa apenas o <b>caminho</b> do Referer (nunca host/esquema) para evitar
     * open redirect. Em caso de dúvida, volta para a home.
     */
    private String caminhoDeRetorno(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                String caminho = URI.create(referer).getPath();
                if (caminho != null
                        && caminho.startsWith("/")
                        && !caminho.startsWith("//")
                        && !caminho.contains("\\")) {
                    return caminho;
                }
            } catch (IllegalArgumentException ignorada) {
                // Referer malformado: cai no destino padrão
            }
        }
        return "/";
    }
}
