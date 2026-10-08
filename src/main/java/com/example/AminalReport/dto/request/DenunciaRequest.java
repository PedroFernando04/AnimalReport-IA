package com.example.AminalReport.dto.request;

import com.example.AminalReport.entities.enums.EnumNivelUrgencia;
import com.example.AminalReport.entities.enums.EnumTipoAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/** Dados do formulário de denúncia de maus-tratos / animal em situação de risco. */
public record DenunciaRequest(

        MultipartFile foto,

        @NotNull(message = "Selecione o tipo do animal.")
        EnumTipoAnimal tipoAnimal,

        @NotBlank(message = "Informe a descrição da situação.")
        String descricao,

        @NotNull(message = "Selecione o nível de urgência.")
        EnumNivelUrgencia nivelUrgencia,

        String cep,

        String rua,

        @NotBlank(message = "Informe o estado.")
        String estado,

        @NotBlank(message = "Informe o município.")
        String municipio,

        @NotBlank(message = "Informe o bairro.")
        String bairro,

        String pontoRef) {
}
