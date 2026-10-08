package com.example.AminalReport.controller;

import com.example.AminalReport.dto.request.DenunciaRequest;
import com.example.AminalReport.dto.request.DenunciaUrgenteRequest;
import com.example.AminalReport.service.DenunciaService;
import com.example.AminalReport.util.AutenticacaoUtils;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DenunciaController {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @GetMapping("/denuncia")
    public String formulario() {
        return "denuncia";
    }

    @PostMapping("/denuncia")
    public String registrar(@Valid @ModelAttribute DenunciaRequest form,
                            @AuthenticationPrincipal UserDetails usuario) {
        denunciaService.registrar(form, AutenticacaoUtils.emailOuNulo(usuario));
        return "redirect:/";
    }

    @GetMapping("/denuncias/detalhe/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        return denunciaService.buscarPorId(id)
                .map(denuncia -> {
                    model.addAttribute("denuncia", denuncia);
                    model.addAttribute("voltarPara", "/home");
                    return "detalhe";
                })
                .orElse("redirect:/");
    }

    @GetMapping("/urgente")
    public String formularioUrgente() {
        return "urgente";
    }

    @PostMapping("/urgente")
    public String registrarUrgente(@Valid @ModelAttribute DenunciaUrgenteRequest form,
                                   @AuthenticationPrincipal UserDetails usuario) {
        denunciaService.registrarUrgente(form, AutenticacaoUtils.emailOuNulo(usuario));
        return "redirect:/";
    }

    @PostMapping("/denuncia/assumir/{id}")
    public String assumir(@PathVariable Long id, @AuthenticationPrincipal UserDetails organizacao) {
        denunciaService.assumir(id, AutenticacaoUtils.emailOuNulo(organizacao));
        return "redirect:/status";
    }
}
