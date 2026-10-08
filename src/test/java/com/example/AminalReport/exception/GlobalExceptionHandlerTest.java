package com.example.AminalReport.exception;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private String tratar(String referer, RedirectAttributesModelMap redirect) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (referer != null) {
            request.addHeader("Referer", referer);
        }
        return handler.tratarRegraDeNegocio(new RegraDeNegocioException("E-mail já cadastrado."), request, redirect);
    }

    @Test
    void voltaParaOCaminhoDoRefererEEnviaMensagemFlash() {
        var redirect = new RedirectAttributesModelMap();

        String destino = tratar("http://localhost:8080/registrar", redirect);

        assertEquals("redirect:/registrar", destino);
        assertEquals("E-mail já cadastrado.", redirect.getFlashAttributes().get(GlobalExceptionHandler.ATRIBUTO_ERRO));
    }

    @Test
    void semRefererVoltaParaHome() {
        assertEquals("redirect:/", tratar(null, new RedirectAttributesModelMap()));
    }

    @Test
    void naoPermiteOpenRedirectViaReferer() {
        // caminho começando com "//" seria interpretado como outro host
        assertEquals("redirect:/", tratar("http://site.com//atacante.com/x", new RedirectAttributesModelMap()));
        assertEquals("redirect:/", tratar("isto nao eh uma url ::", new RedirectAttributesModelMap()));
    }
}
