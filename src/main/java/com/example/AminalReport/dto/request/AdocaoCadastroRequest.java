package com.example.AminalReport.dto.request;

import com.example.AminalReport.entities.enums.EnumTipoAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.multipart.MultipartFile;

/** Dados do formulário de cadastro de animal para adoção. */
public record AdocaoCadastroRequest(

        @NotBlank(message = "Informe o nome do animal.")
        String nomeAnimal,

        @NotNull(message = "Informe a idade estimada.")
        @PositiveOrZero(message = "A idade não pode ser negativa.")
        Integer idadeEstimada,

        @NotBlank(message = "Informe a descrição do animal.")
        String descricao,

        MultipartFile foto,

        @NotNull(message = "Selecione o tipo do animal.")
        EnumTipoAnimal tipoAnimal,

        @NotBlank(message = "Informe um contato.")
        String contato) {
}
