package com.example.AminalReport.mapper;

import com.example.AminalReport.dto.request.DenunciaAlteracaoRequest;
import com.example.AminalReport.dto.request.DenunciaRequest;
import com.example.AminalReport.dto.request.DenunciaUrgenteRequest;
import com.example.AminalReport.dto.response.DenunciaResponse;
import com.example.AminalReport.entities.enums.EnumNivelUrgencia;
import com.example.AminalReport.entities.enums.EnumTipoAnimal;
import com.example.AminalReport.entities.formularios.Denuncia;
import com.example.AminalReport.entities.usuarios.Organizacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DenunciaMapperTest {

    private final DenunciaMapper mapper = new DenunciaMapper();

    @Test
    void toEntityMapeiaNivelUrgenciaParaUrgenciaEEndereco() {
        var request = new DenunciaRequest(null, EnumTipoAnimal.GATO, "Ferido", EnumNivelUrgencia.MUITO_URGENTE,
                "57000-000", "Rua A", "AL", "Maceió", "Centro", "Perto da praça");

        Denuncia denuncia = mapper.toEntity(request);

        assertEquals(EnumNivelUrgencia.MUITO_URGENTE, denuncia.getUrgencia());
        assertEquals("Maceió", denuncia.getMunicipio());
        assertEquals("Centro", denuncia.getBairro());
        assertEquals("Perto da praça", denuncia.getPontoRef());
        // andamento inicial é regra de negócio do service
        assertNull(denuncia.getAndamentoDenuncia());
    }

    @Test
    void toEntityUrgenteCopiaApenasCamposInformados() {
        var request = new DenunciaUrgenteRequest(null, EnumTipoAnimal.CACHORRO, "Atropelado", "8299999-0000");

        Denuncia denuncia = mapper.toEntity(request);

        assertEquals("8299999-0000", denuncia.getContato());
        assertNull(denuncia.getCep());
        assertNull(denuncia.getUrgencia());
    }

    @Test
    void atualizarEntidadeIgnoraCamposNaoInformados() {
        Denuncia denuncia = new Denuncia();
        denuncia.setDescricao("Original");
        denuncia.setUrgencia(EnumNivelUrgencia.URGENTE);
        denuncia.setPontoRef("Ref");

        mapper.atualizarEntidade(denuncia, new DenunciaAlteracaoRequest("", EnumNivelUrgencia.EMERGENCIA, null, null));

        assertEquals("Original", denuncia.getDescricao());
        assertEquals(EnumNivelUrgencia.EMERGENCIA, denuncia.getUrgencia());
        assertEquals("Ref", denuncia.getPontoRef());
    }

    @Test
    void toResponseIdentificaDenunciaRapidaPeloCepFixo() {
        Denuncia rapida = new Denuncia();
        rapida.setCep(Denuncia.CEP_DENUNCIA_URGENTE);
        Denuncia comum = new Denuncia();
        comum.setCep("57000-000");

        assertTrue(mapper.toResponse(rapida).denunciaRapida());
        assertFalse(mapper.toResponse(comum).denunciaRapida());
    }

    @Test
    void toResponseExpoeResumoDaOrganizacaoResponsavel() {
        Organizacao ong = new Organizacao();
        ong.setNome("ONG Patas");
        Denuncia denuncia = new Denuncia();
        denuncia.setOrganizacaoResponsavel(ong);

        DenunciaResponse response = mapper.toResponse(denuncia);

        assertEquals("ONG Patas", response.organizacaoResponsavel().nome());
        assertNull(mapper.toResponse(new Denuncia()).organizacaoResponsavel());
    }
}
