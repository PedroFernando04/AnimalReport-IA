package com.example.AminalReport.controller;

import com.example.AminalReport.dto.request.UsuarioAlteracaoRequest;
import com.example.AminalReport.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alterarUsuario")
public class UsuarioController {

    private final UserService userService;

    public UsuarioController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String formulario() {
        return "alterarUsuario";
    }

    @PostMapping("/{id}")
    public String alterar(@PathVariable Long id,
                          @Valid @ModelAttribute UsuarioAlteracaoRequest form,
                          @AuthenticationPrincipal UserDetails usuario) {
        userService.alterar(id, form, usuario.getUsername());
        return "redirect:/home";
    }
}
