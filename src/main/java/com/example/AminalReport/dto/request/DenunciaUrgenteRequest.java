package com.example.AminalReport.dto.request;

import com.example.AminalReport.entities.enums.EnumTipoAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/** Formulário simplificado de denúncia urgente (sem endereço, apenas contato). */
public record DenunciaUrgenteRequest(

        MultipartFile foto,

        @NotNull(message = "Selecione o tipo do animal.")
        EnumTipoAnimal tipoAnimal,

        @NotBlank(message = "Informe a descrição da situação.")
        String descricao,

        @NotBlank(message = "Informe um contato.")
        String contato) {
}
