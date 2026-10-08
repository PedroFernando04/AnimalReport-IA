package com.example.AminalReport.controller;

import com.example.AminalReport.dto.request.AdocaoAlteracaoRequest;
import com.example.AminalReport.dto.request.AdocaoCadastroRequest;
import com.example.AminalReport.dto.response.AdocaoResponse;
import com.example.AminalReport.entities.enums.EnumAndamentoAdocao;
import com.example.AminalReport.service.AdocaoService;
import com.example.AminalReport.util.AutenticacaoUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/adocao")
public class AdocaoController {

    private final AdocaoService adocaoService;

    public AdocaoController(AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
    }

    @GetMapping("/cadastrar")
    public String formularioCadastro() {
        return "adocaoCadastro";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@Valid @ModelAttribute AdocaoCadastroRequest form,
                            @AuthenticationPrincipal UserDetails usuario) {
        adocaoService.cadastrar(form, AutenticacaoUtils.emailOuNulo(usuario));
        return "redirect:/adocao/minhasDoacoes";
    }

    @GetMapping("/home")
    public String listar(@RequestParam(defaultValue = "0") int pagina, Model model) {
        Page<AdocaoResponse> adocoes = adocaoService.listarPaginado(pagina);

        model.addAttribute("listaAdocao", adocoes.getContent());
        model.addAttribute("currentPage", pagina);
        model.addAttribute("totalPages", adocoes.getTotalPages());
        return "adocaoHome";
    }

    @GetMapping("/home/detalhe/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        return adocaoService.buscarPorId(id)
                .map(adocao -> {
                    model.addAttribute("adocao", adocao);
                    model.addAttribute("voltarPara", "/adocao/home");
                    return "adocaoDetalhe";
                })
                .orElse("redirect:/adocao/home");
    }

    @GetMapping("/minhasDoacoes")
    public String minhasDoacoes(@AuthenticationPrincipal UserDetails usuario, Model model) {
        model.addAttribute("minhasAdocoes",
                adocaoService.listarPorUsuario(AutenticacaoUtils.emailOuNulo(usuario)));
        return "adocaoMinhas";
    }

    @GetMapping("/alterar/{id}")
    public String formularioAlteracao(@PathVariable Long id, Model model) {
        return adocaoService.buscarPorId(id)
                .map(adocao -> {
                    model.addAttribute("adocao", adocao);
                    model.addAttribute("statusExistentes", EnumAndamentoAdocao.values());
                    return "adocaoAlterar";
                })
                .orElse("redirect:/adocao/home");
    }

    @PostMapping("/alterar/{id}")
    public String alterar(@PathVariable Long id,
                          @Valid @ModelAttribute AdocaoAlteracaoRequest form) {
        adocaoService.alterar(id, form);
        return "redirect:/adocao/home/detalhe/" + id;
    }
}
