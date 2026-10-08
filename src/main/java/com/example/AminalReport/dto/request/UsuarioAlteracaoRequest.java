package com.example.AminalReport.dto.request;

import com.example.AminalReport.util.TextoUtils;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * Atualização parcial do perfil: somente os campos informados são alterados.
 * Strings em branco são normalizadas para {@code null} ("não informado").
 */
public record UsuarioAlteracaoRequest(

        MultipartFile foto,

        String nome,

        @Email(message = "E-mail inválido.")
        String email,

        @Pattern(regexp = "^\\D*(\\d\\D*){10,11}$", message = "Telefone inválido.")
        String telefone,

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String senha,

        String confirmarSenha) {

    public UsuarioAlteracaoRequest {
        nome = TextoUtils.vazioParaNulo(nome);
        email = TextoUtils.vazioParaNulo(email);
        telefone = TextoUtils.vazioParaNulo(telefone);
        senha = TextoUtils.vazioParaNulo(senha);
        confirmarSenha = TextoUtils.vazioParaNulo(confirmarSenha);
    }

    @AssertTrue(message = "As senhas não conferem.")
    public boolean isSenhasConferem() {
        return Objects.equals(senha, confirmarSenha);
    }
}
