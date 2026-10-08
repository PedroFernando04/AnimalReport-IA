package com.example.AminalReport.controller;

import com.example.AminalReport.dto.request.RegistroComumRequest;
import com.example.AminalReport.dto.request.RegistroOrganizacaoRequest;
import com.example.AminalReport.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Telas de login e cadastro. A autenticação em si (POST /entrar) é feita pelo
 * filtro de formulário do Spring Security, configurado em {@code SecurityConfig}.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/entrar")
    public String login() {
        return "login";
    }

    @GetMapping("/registrar")
    public String formularioRegistro() {
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute RegistroComumRequest form) {
        userService.registrarComum(form);
        return "redirect:/entrar";
    }

    @GetMapping("/registrarOrg")
    public String formularioRegistroOrganizacao() {
        return "registrarOrg";
    }

    @PostMapping("/registrarOrg")
    public String registrarOrganizacao(@Valid @ModelAttribute RegistroOrganizacaoRequest form) {
        userService.registrarOrganizacao(form);
        return "redirect:/entrar";
    }
}
