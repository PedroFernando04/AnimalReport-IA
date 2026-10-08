package com.example.AminalReport.config;

import com.example.AminalReport.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/** Atributos disponíveis em todas as views (navbar, destaque de menu, etc.). */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute
    public void adicionarUsuarioLogado(Model model, Principal principal) {
        if (principal == null) {
            return;
        }

        userService.buscarLogado(principal.getName()).ifPresent(usuario -> {
            model.addAttribute("usuarioLogado", usuario);
            model.addAttribute("fotoPerfil", usuario.fotoUrl());
        });
    }
}
