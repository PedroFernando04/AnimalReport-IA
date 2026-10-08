package com.example.AminalReport.mapper;

import com.example.AminalReport.dto.request.AdocaoAlteracaoRequest;
import com.example.AminalReport.dto.request.AdocaoCadastroRequest;
import com.example.AminalReport.dto.response.AdocaoResponse;
import com.example.AminalReport.entities.enums.EnumAndamentoAdocao;
import com.example.AminalReport.entities.enums.EnumTipoAnimal;
import com.example.AminalReport.entities.formularios.Adocao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdocaoMapperTest {

    private final AdocaoMapper mapper = new AdocaoMapper();

    @Test
    void toEntityCopiaCamposDoFormulario() {
        var request = new AdocaoCadastroRequest("Rex", 3, "Dócil", null, EnumTipoAnimal.CACHORRO, "8299999-0000");

        Adocao adocao = mapper.toEntity(request);

        assertEquals("Rex", adocao.getNomeAnimal());
        assertEquals(3, adocao.getIdadeEstimada());
        assertEquals("Dócil", adocao.getDescricao());
        assertEquals(EnumTipoAnimal.CACHORRO, adocao.getTipoAnimal());
        assertEquals("8299999-0000", adocao.getContato());
        // criador, foto e status são responsabilidade do service
        assertNull(adocao.getStatusAdocao());
        assertNull(adocao.getFoto());
    }

    @Test
    void atualizarEntidadeAlteraSomenteCamposInformados() {
        Adocao adocao = new Adocao();
        adocao.setNomeAnimal("Rex");
        adocao.setIdadeEstimada(3);
        adocao.setDescricao("Dócil");
        adocao.setContato("111");

        // campos em branco chegam do formulário como "" e viram null no DTO
        var request = new AdocaoAlteracaoRequest(null, "", 4, "  ", null, EnumAndamentoAdocao.CONCLUIDA);

        mapper.atualizarEntidade(adocao, request);

        assertEquals("Rex", adocao.getNomeAnimal());
        assertEquals(4, adocao.getIdadeEstimada());
        assertEquals("Dócil", adocao.getDescricao());
        assertEquals("111", adocao.getContato());
        assertEquals(EnumAndamentoAdocao.CONCLUIDA, adocao.getStatusAdocao());
    }

    @Test
    void toResponseUsaImagemPadraoQuandoNaoHaFoto() {
        Adocao adocao = new Adocao();
        adocao.setNomeAnimal("Rex");

        AdocaoResponse response = mapper.toResponse(adocao);

        assertEquals("/images/sem-foto.jpg", response.imagemUrl());
        assertNull(response.foto());
    }

    @Test
    void toResponseMontaUrlDaFotoEnviada() {
        Adocao adocao = new Adocao();
        adocao.setFoto("adocao/abc_rex.png");

        assertEquals("/uploads/adocao/abc_rex.png", mapper.toResponse(adocao).imagemUrl());
    }
}
