package com.example.AminalReport.controller;

import com.example.AminalReport.dto.request.DenunciaAlteracaoRequest;
import com.example.AminalReport.entities.enums.EnumNivelUrgencia;
import com.example.AminalReport.service.DenunciaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/status")
public class StatusController {

    private final DenunciaService denunciaService;

    public StatusController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @GetMapping
    public String status(@AuthenticationPrincipal UserDetails usuario, Model model) {
        model.addAttribute("denuncias", denunciaService.listarParaStatus(usuario.getUsername()));
        return "status";
    }

    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        return denunciaService.buscarPorId(id)
                .map(denuncia -> {
                    model.addAttribute("denuncia", denuncia);
                    model.addAttribute("voltarPara", "/status");
                    return "detalhe";
                })
                .orElse("redirect:/status");
    }

    @GetMapping("/editar/{id}")
    public String formularioEdicao(@PathVariable Long id, Model model) {
        return denunciaService.buscarPorId(id)
                .map(denuncia -> {
                    model.addAttribute("denuncia", denuncia);
                    model.addAttribute("nivelUrgencias", EnumNivelUrgencia.values());
                    return "alterarDenuncia";
                })
                .orElse("redirect:/status");
    }

    @PostMapping("/editar/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute DenunciaAlteracaoRequest form) {
        denunciaService.atualizar(id, form);
        return "redirect:/status";
    }
}
