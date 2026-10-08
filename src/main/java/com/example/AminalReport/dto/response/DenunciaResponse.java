package com.example.AminalReport.dto.response;

import com.example.AminalReport.entities.enums.EnumAndamentoDenuncia;
import com.example.AminalReport.entities.enums.EnumNivelUrgencia;
import com.example.AminalReport.entities.enums.EnumTipoAnimal;

/**
 * Dados de uma denúncia prontos para as views.
 *
 * @param imagemUrl      URL pronta para uso em {@code <img>}, já com imagem padrão quando não há foto
 * @param denunciaRapida {@code true} quando veio do formulário de denúncia urgente
 *                       (sem endereço; exibe o contato em vez da localização)
 */
public record DenunciaResponse(
        Long id,
        String foto,
        String imagemUrl,
        EnumTipoAnimal tipoAnimal,
        String descricao,
        String contato,
        EnumNivelUrgencia urgencia,
        EnumAndamentoDenuncia andamentoDenuncia,
        String cep,
        String rua,
        String bairro,
        String municipio,
        String estado,
        String pontoRef,
        boolean denunciaRapida,
        OrganizacaoResumoResponse organizacaoResponsavel) {
}
