package com.example.AminalReport.service;

import com.example.AminalReport.dto.request.AdocaoAlteracaoRequest;
import com.example.AminalReport.dto.request.AdocaoCadastroRequest;
import com.example.AminalReport.dto.response.AdocaoResponse;
import com.example.AminalReport.entities.enums.EnumAndamentoAdocao;
import com.example.AminalReport.entities.formularios.Adocao;
import com.example.AminalReport.entities.usuarios.Usuario;
import com.example.AminalReport.exception.RecursoNaoEncontradoException;
import com.example.AminalReport.exception.RegraDeNegocioException;
import com.example.AminalReport.mapper.AdocaoMapper;
import com.example.AminalReport.repository.formularios.AdocaoRepository;
import com.example.AminalReport.repository.usuarios.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdocaoService {

    private static final String PASTA_UPLOAD = "adocao";
    private static final int TAMANHO_PAGINA = 6;

    private final AdocaoRepository adocaoRepository;
    private final UserRepository userRepository;
    private final UploadService uploadService;
    private final AdocaoMapper adocaoMapper;

    public AdocaoService(AdocaoRepository adocaoRepository,
                         UserRepository userRepository,
                         UploadService uploadService,
                         AdocaoMapper adocaoMapper) {
        this.adocaoRepository = adocaoRepository;
        this.userRepository = userRepository;
        this.uploadService = uploadService;
        this.adocaoMapper = adocaoMapper;
    }

    /**
     * @param emailCriador e-mail do usuário autenticado; {@code null} para visitante anônimo
     */
    @Transactional
    public void cadastrar(AdocaoCadastroRequest request, String emailCriador) {
        Usuario criador = buscarUsuario(emailCriador);

        if (adocaoRepository.findByNomeAnimalAndUsuarioCriador(request.nomeAnimal(), criador).isPresent()) {
            throw new RegraDeNegocioException("Registro de adoção já cadastrado.");
        }

        Adocao adocao = adocaoMapper.toEntity(request);
        adocao.setUsuarioCriador(criador);
        adocao.setStatusAdocao(EnumAndamentoAdocao.AGUARDANDO);
        adocao.setFoto(uploadService.salvarImagem(request.foto(), PASTA_UPLOAD));

        adocaoRepository.save(adocao);
    }

    @Transactional(readOnly = true)
    public Page<AdocaoResponse> listarPaginado(int pagina) {
        Pageable pageable = PageRequest.of(Math.max(pagina, 0), TAMANHO_PAGINA, Sort.by("id").descending());
        return adocaoRepository.findAll(pageable).map(adocaoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AdocaoResponse> listarDestaques() {
        return adocaoMapper.toResponseList(adocaoRepository.findTop5ByOrderByIdDesc());
    }

    /** Adoções cadastradas pelo usuário; lista vazia para visitante anônimo. */
    @Transactional(readOnly = true)
    public List<AdocaoResponse> listarPorUsuario(String email) {
        Usuario usuario = buscarUsuario(email);
        if (usuario == null) {
            return List.of();
        }
        return adocaoMapper.toResponseList(adocaoRepository.findByUsuarioCriador(usuario));
    }

    @Transactional(readOnly = true)
    public Optional<AdocaoResponse> buscarPorId(Long id) {
        return adocaoRepository.findById(id).map(adocaoMapper::toResponse);
    }

    @Transactional
    public void alterar(Long id, AdocaoAlteracaoRequest request) {
        Adocao adocao = adocaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Adoção não encontrada: " + id));

        adocaoMapper.atualizarEntidade(adocao, request);
        adocao.setFoto(uploadService.substituirImagem(adocao.getFoto(), request.foto(), PASTA_UPLOAD));

        adocaoRepository.save(adocao);
    }

    private Usuario buscarUsuario(String email) {
        return email == null ? null : userRepository.findByEmail(email).orElse(null);
    }
}
