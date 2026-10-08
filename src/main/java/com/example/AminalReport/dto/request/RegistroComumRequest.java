package com.example.AminalReport.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/** Cadastro de usuário comum (pessoa física). */
public record RegistroComumRequest(

        @NotBlank(message = "Informe o nome.")
        String nome,

        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "E-mail inválido.")
        String email,

        // Aceita máscara: exige 10 ou 11 dígitos, ignorando os demais caracteres
        @NotBlank(message = "Informe o telefone.")
        @Pattern(regexp = "^\\D*(\\d\\D*){10,11}$", message = "Telefone inválido.")
        String telefone,

        // Exige exatamente 11 dígitos, ignorando a máscara (000.000.000-00)
        @NotBlank(message = "Informe o CPF.")
        @Pattern(regexp = "^\\D*(\\d\\D*){11}$", message = "CPF inválido.")
        String cpf,

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
