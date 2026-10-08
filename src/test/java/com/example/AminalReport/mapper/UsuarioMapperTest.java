package com.example.AminalReport.mapper;

import com.example.AminalReport.dto.request.RegistroComumRequest;
import com.example.AminalReport.dto.request.RegistroOrganizacaoRequest;
import com.example.AminalReport.dto.request.UsuarioAlteracaoRequest;
import com.example.AminalReport.dto.response.UsuarioLogadoResponse;
import com.example.AminalReport.entities.enums.EnumTipoOrg;
import com.example.AminalReport.entities.usuarios.Comum;
import com.example.AminalReport.entities.usuarios.Organizacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void toEntityComumRemoveMascaras() {
        var request = new RegistroComumRequest("Ana", "ana@x.com", "(82) 99999-0000",
                "123.456.789-09", "senha1234", "senha1234");

        Comum comum = mapper.toEntity(request);

        assertEquals("12345678909", comum.getCpf());
        assertEquals("82999990000", comum.getTelefone());
        // a senha só é definida (criptografada) pelo service
        assertNull(comum.getSenha());
    }

    @Test
    void toEntityOrganizacaoRemoveMascaras() {
        var request = new RegistroOrganizacaoRequest("ONG", "ong@x.com", "(82) 3333-0000",
                "12.345.678/0001-90", "123.456.789", EnumTipoOrg.ONG, "senha1234", "senha1234");

        Organizacao org = mapper.toEntity(request);

        assertEquals("12345678000190", org.getCnpj());
        assertEquals("123456789", org.getInscricaoEstadual());
        assertEquals(EnumTipoOrg.ONG, org.getTipoOrg());
    }

    @Test
    void atualizarEntidadeNaoSobrescreveComCamposVazios() {
        Comum comum = new Comum();
        comum.setNome("Ana");
        comum.setEmail("ana@x.com");
        comum.setTelefone("82999990000");

        mapper.atualizarEntidade(comum, new UsuarioAlteracaoRequest(null, "Ana Maria", "", "", null, null));

        assertEquals("Ana Maria", comum.getNome());
        assertEquals("ana@x.com", comum.getEmail());
        assertEquals("82999990000", comum.getTelefone());
    }

    @Test
    void toLogadoResponseDeUsuarioComum() {
        Comum comum = new Comum();
        comum.setNome("Ana");
        comum.setCpf("12345678909");

        UsuarioLogadoResponse response = mapper.toLogadoResponse(comum);

        assertEquals("Comum", response.tipoUsuario());
        assertEquals("123.456.789-09", response.cpfFormatado());
        assertNull(response.cnpjFormatado());
        assertEquals("/images/perfilPadrao.jpg", response.fotoUrl());
    }

    @Test
    void toLogadoResponseDeOrganizacao() {
        Organizacao org = new Organizacao();
        org.setNome("ONG");
        org.setCnpj("12345678000190");
        org.setTipoOrg(EnumTipoOrg.ONG);
        org.setFoto("usuario/abc.png");

        UsuarioLogadoResponse response = mapper.toLogadoResponse(org);

        assertEquals("Organizacao", response.tipoUsuario());
        assertEquals("12.345.678/0001-90", response.cnpjFormatado());
        assertEquals(EnumTipoOrg.ONG, response.tipoOrg());
        assertNull(response.cpfFormatado());
        assertEquals("/uploads/usuario/abc.png", response.fotoUrl());
    }
}
