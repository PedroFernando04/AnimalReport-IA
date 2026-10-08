package com.example.AminalReport.mapper;

import com.example.AminalReport.dto.request.DenunciaAlteracaoRequest;
import com.example.AminalReport.dto.request.DenunciaRequest;
import com.example.AminalReport.dto.request.DenunciaUrgenteRequest;
import com.example.AminalReport.dto.response.DenunciaResponse;
import com.example.AminalReport.dto.response.OrganizacaoResumoResponse;
import com.example.AminalReport.entities.formularios.Denuncia;
import com.example.AminalReport.entities.usuarios.Organizacao;
import com.example.AminalReport.util.ImagemUrls;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DenunciaMapper {

    /**
     * Cria a entidade a partir do formulário completo. Foto, criador e andamento
     * inicial dependem de regras de negócio e são definidos pelo service.
     */
    public Denuncia toEntity(DenunciaRequest request) {
        Denuncia denuncia = new Denuncia();
        denuncia.setTipoAnimal(request.tipoAnimal());
        denuncia.setDescricao(request.descricao());
        denuncia.setUrgencia(request.nivelUrgencia());
        denuncia.setCep(request.cep());
        denuncia.setEstado(request.estado());
        denuncia.setMunicipio(request.municipio());
        denuncia.setBairro(request.bairro());
        denuncia.setRua(request.rua());
        denuncia.setPontoRef(request.pontoRef());
        return denuncia;
    }

    /**
     * Cria a entidade a partir do formulário urgente. Os valores fixos de
     * localização/urgência são regra de negócio e ficam no service.
     */
    public Denuncia toEntity(DenunciaUrgenteRequest request) {
        Denuncia denuncia = new Denuncia();
        denuncia.setTipoAnimal(request.tipoAnimal());
        denuncia.setDescricao(request.descricao());
        denuncia.setContato(request.contato());
        return denuncia;
    }

    /** Aplica apenas os campos informados (não nulos). A foto é tratada no service. */
    public void atualizarEntidade(Denuncia denuncia, DenunciaAlteracaoRequest request) {
        if (request.descricao() != null) {
            denuncia.setDescricao(request.descricao());
        }
        if (request.nivelUrgencia() != null) {
            denuncia.setUrgencia(request.nivelUrgencia());
        }
        if (request.pontoRef() != null) {
            denuncia.setPontoRef(request.pontoRef());
        }
    }

    public DenunciaResponse toResponse(Denuncia denuncia) {
        return new DenunciaResponse(
                denuncia.getId(),
                denuncia.getFoto(),
                ImagemUrls.doAnimal(denuncia.getFoto()),
                denuncia.getTipoAnimal(),
                denuncia.getDescricao(),
                denuncia.getContato(),
                denuncia.getUrgencia(),
                denuncia.getAndamentoDenuncia(),
                denuncia.getCep(),
                denuncia.getRua(),
                denuncia.getBairro(),
                denuncia.getMunicipio(),
                denuncia.getEstado(),
                denuncia.getPontoRef(),
                Denuncia.CEP_DENUNCIA_URGENTE.equals(denuncia.getCep()),
                toResumo(denuncia.getOrganizacaoResponsavel()));
    }

    public List<DenunciaResponse> toResponseList(List<Denuncia> denuncias) {
        return denuncias.stream().map(this::toResponse).toList();
    }

    private OrganizacaoResumoResponse toResumo(Organizacao organizacao) {
        return organizacao == null
                ? null
                : new OrganizacaoResumoResponse(organizacao.getId(), organizacao.getNome());
    }
}
