package com.example.AminalReport.dto.request;

import com.example.AminalReport.entities.enums.EnumTipoOrg;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/** Cadastro de organização (ONG, secretaria municipal, polícia ambiental). */
public record RegistroOrganizacaoRequest(

        @NotBlank(message = "Informe o nome da organização.")
        String nome,

        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Informe o telefone.")
        @Pattern(regexp = "^\\D*(\\d\\D*){10,11}$", message = "Telefone inválido.")
        String telefone,

        // Exige exatamente 14 dígitos, ignorando a máscara (00.000.000/0000-00)
        @NotBlank(message = "Informe o CNPJ.")
        @Pattern(regexp = "^\\D*(\\d\\D*){14}$", message = "CNPJ inválido.")
        String cnpj,

        // A coluna comporta até 14 dígitos
        @NotBlank(message = "Informe a inscrição estadual.")
        @Pattern(regexp = "^\\D*(\\d\\D*){1,14}$", message = "Inscrição estadual inválida.")
        String inscricaoEstadual,

        @NotNull(message = "Selecione o tipo da organização.")
        EnumTipoOrg tipoOrg,

        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String senha,

        @NotBlank(message = "Confirme a senha.")
        String confirmarSenha) {

    @AssertTrue(message = "As senhas não conferem.")
    public boolean isSenhasConferem() {
        return senha == null || confirmarSenha == null || Objects.equals(senha, confirmarSenha);
    }
}
