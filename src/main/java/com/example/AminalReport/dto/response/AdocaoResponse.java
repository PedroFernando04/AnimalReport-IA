package com.example.AminalReport.dto.response;

import com.example.AminalReport.entities.enums.EnumAndamentoAdocao;
import com.example.AminalReport.entities.enums.EnumTipoAnimal;

/**
 * Dados de uma adoção prontos para as views.
 *
 * @param foto      caminho relativo salvo no banco (ex.: {@code adocao/uuid_foto.jpg})
 * @param imagemUrl URL pronta para uso em {@code <img>}, já com imagem padrão quando não há foto
 */
public record AdocaoResponse(
        Long id,
        String nomeAnimal,
        Integer idadeEstimada,
        String descricao,
        String foto,
        String imagemUrl,
        EnumTipoAnimal tipoAnimal,
        String contato,
        EnumAndamentoAdocao statusAdocao) {
}
