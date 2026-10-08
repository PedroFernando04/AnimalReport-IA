package com.example.AminalReport.dto.request;

import com.example.AminalReport.entities.enums.EnumNivelUrgencia;
import com.example.AminalReport.util.TextoUtils;
import org.springframework.web.multipart.MultipartFile;

/** Atualização parcial de uma denúncia: somente os campos informados são alterados. */
public record DenunciaAlteracaoRequest(

        String descricao,

        EnumNivelUrgencia nivelUrgencia,

        String pontoRef,

        MultipartFile foto) {

    public DenunciaAlteracaoRequest {
        descricao = TextoUtils.vazioParaNulo(descricao);
        pontoRef = TextoUtils.vazioParaNulo(pontoRef);
    }
}
