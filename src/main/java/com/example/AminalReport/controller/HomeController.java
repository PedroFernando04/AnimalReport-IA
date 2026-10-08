package com.example.AminalReport.controller;

import com.example.AminalReport.service.AdocaoService;
import com.example.AminalReport.service.DenunciaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DenunciaService denunciaService;
    private final AdocaoService adocaoService;

    public HomeController(DenunciaService denunciaService, AdocaoService adocaoService) {
        this.denunciaService = denunciaService;
        this.adocaoService = adocaoService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("denuncias", denunciaService.listarPendentes());
        model.addAttribute("adocoesDestaque", adocaoService.listarDestaques());
        return "home";
    }
}
