package com.example.AminalReport.mapper;

import com.example.AminalReport.dto.request.AdocaoAlteracaoRequest;
import com.example.AminalReport.dto.request.AdocaoCadastroRequest;
import com.example.AminalReport.dto.response.AdocaoResponse;
import com.example.AminalReport.entities.formularios.Adocao;
import com.example.AminalReport.util.ImagemUrls;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdocaoMapper {

    /**
     * Cria a entidade a partir do formulário. Foto, criador e status inicial
     * dependem de regras de negócio e são definidos pelo service.
     */
    public Adocao toEntity(AdocaoCadastroRequest request) {
        Adocao adocao = new Adocao();
        adocao.setNomeAnimal(request.nomeAnimal());
        adocao.setIdadeEstimada(request.idadeEstimada());
        adocao.setDescricao(request.descricao());
        adocao.setTipoAnimal(request.tipoAnimal());
        adocao.setContato(request.contato());
        return adocao;
    }

    /** Aplica apenas os campos informados (não nulos). A foto é tratada no service. */
    public void atualizarEntidade(Adocao adocao, AdocaoAlteracaoRequest request) {
        if (request.nomeAnimal() != null) {
            adocao.setNomeAnimal(request.nomeAnimal());
        }
        if (request.idadeEstimada() != null) {
            adocao.setIdadeEstimada(request.idadeEstimada());
        }
        if (request.descricao() != null) {
            adocao.setDescricao(request.descricao());
        }
        if (request.contato() != null) {
            adocao.setContato(request.contato());
        }
        if (request.statusAdocao() != null) {
            adocao.setStatusAdocao(request.statusAdocao());
        }
    }

    public AdocaoResponse toResponse(Adocao adocao) {
        return new AdocaoResponse(
                adocao.getId(),
                adocao.getNomeAnimal(),
                adocao.getIdadeEstimada(),
                adocao.getDescricao(),
                adocao.getFoto(),
                ImagemUrls.doAnimal(adocao.getFoto()),
                adocao.getTipoAnimal(),
                adocao.getContato(),
                adocao.getStatusAdocao());
    }

    public List<AdocaoResponse> toResponseList(List<Adocao> adocoes) {
        return adocoes.stream().map(this::toResponse).toList();
    }
}
