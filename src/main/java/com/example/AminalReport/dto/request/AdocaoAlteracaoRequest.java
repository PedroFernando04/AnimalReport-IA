package com.example.AminalReport.dto.request;

import com.example.AminalReport.entities.enums.EnumAndamentoAdocao;
import com.example.AminalReport.util.TextoUtils;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.multipart.MultipartFile;

/**
 * Atualização parcial de uma adoção: somente os campos informados são alterados.
 * Strings em branco são normalizadas para {@code null} ("não informado").
 */
public record AdocaoAlteracaoRequest(

        MultipartFile foto,

        String nomeAnimal,

        @PositiveOrZero(message = "A idade não pode ser negativa.")
        Integer idadeEstimada,

        String descricao,

        String contato,

        EnumAndamentoAdocao statusAdocao) {

    public AdocaoAlteracaoRequest {
        nomeAnimal = TextoUtils.vazioParaNulo(nomeAnimal);
        descricao = TextoUtils.vazioParaNulo(descricao);
        contato = TextoUtils.vazioParaNulo(contato);
    }
}
