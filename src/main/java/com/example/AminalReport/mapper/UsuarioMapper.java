package com.example.AminalReport.mapper;

import com.example.AminalReport.dto.request.RegistroComumRequest;
import com.example.AminalReport.dto.request.RegistroOrganizacaoRequest;
import com.example.AminalReport.dto.request.UsuarioAlteracaoRequest;
import com.example.AminalReport.dto.response.UsuarioLogadoResponse;
import com.example.AminalReport.entities.enums.EnumTipoOrg;
import com.example.AminalReport.entities.usuarios.Comum;
import com.example.AminalReport.entities.usuarios.Organizacao;
import com.example.AminalReport.entities.usuarios.Usuario;
import com.example.AminalReport.util.ImagemUrls;
import com.example.AminalReport.util.TextoUtils;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    /**
     * Cria o usuário comum a partir do formulário. Senha (criptografia),
     * data de cadastro e status inicial são definidos pelo service.
     */
    public Comum toEntity(RegistroComumRequest request) {
        Comum comum = new Comum();
        comum.setNome(request.nome());
        comum.setEmail(request.email());
        comum.setTelefone(request.telefone());
        comum.setCpf(TextoUtils.somenteDigitos(request.cpf()));
        return comum;
    }

    /**
     * Cria a organização a partir do formulário. Senha (criptografia),
     * data de cadastro e status inicial são definidos pelo service.
     */
    public Organizacao toEntity(RegistroOrganizacaoRequest request) {
        Organizacao organizacao = new Organizacao();
        organizacao.setNome(request.nome());
        organizacao.setEmail(request.email());
        organizacao.setTelefone(request.telefone());
        organizacao.setCnpj(TextoUtils.somenteDigitos(request.cnpj()));
        organizacao.setInscricaoEstadual(TextoUtils.somenteDigitos(request.inscricaoEstadual()));
        organizacao.setTipoOrg(request.tipoOrg());
        return organizacao;
    }

    /** Aplica apenas os campos informados. Senha e foto são tratadas no service. */
    public void atualizarEntidade(Usuario usuario, UsuarioAlteracaoRequest request) {
        if (request.nome() != null) {
            usuario.setNome(request.nome());
        }
        if (request.email() != null) {
            usuario.setEmail(request.email());
        }
        if (request.telefone() != null) {
            usuario.setTelefone(request.telefone());
        }
    }

    public UsuarioLogadoResponse toLogadoResponse(Usuario usuario) {
        String cpfFormatado = null;
        String cnpjFormatado = null;
        String inscricaoEstadual = null;
        EnumTipoOrg tipoOrg = null;

        if (usuario instanceof Comum comum) {
            cpfFormatado = comum.getCpfFormatado();
        } else if (usuario instanceof Organizacao organizacao) {
            cnpjFormatado = organizacao.getCnpjFormatado();
            inscricaoEstadual = organizacao.getInscricaoEstadual();
            tipoOrg = organizacao.getTipoOrg();
        }

        return new UsuarioLogadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefoneFormatado(),
                usuario.getFoto(),
                ImagemUrls.doPerfil(usuario.getFoto()),
                tipoUsuarioDe(usuario),
                cpfFormatado,
                cnpjFormatado,
                inscricaoEstadual,
                tipoOrg);
    }

    /** Mesmos valores que os templates já comparam: "Comum" e "Organizacao". */
    private String tipoUsuarioDe(Usuario usuario) {
        if (usuario instanceof Comum) {
            return "Comum";
        }
        if (usuario instanceof Organizacao) {
            return "Organizacao";
        }
        return usuario.getTipoUsuario();
    }
}
