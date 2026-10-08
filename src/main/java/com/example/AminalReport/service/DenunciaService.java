package com.example.AminalReport.service;

import com.example.AminalReport.dto.request.DenunciaAlteracaoRequest;
import com.example.AminalReport.dto.request.DenunciaRequest;
import com.example.AminalReport.dto.request.DenunciaUrgenteRequest;
import com.example.AminalReport.dto.response.DenunciaResponse;
import com.example.AminalReport.entities.enums.EnumAndamentoDenuncia;
import com.example.AminalReport.entities.enums.EnumNivelUrgencia;
import com.example.AminalReport.entities.formularios.Denuncia;
import com.example.AminalReport.entities.usuarios.Comum;
import com.example.AminalReport.entities.usuarios.Organizacao;
import com.example.AminalReport.entities.usuarios.Usuario;
import com.example.AminalReport.exception.RecursoNaoEncontradoException;
import com.example.AminalReport.exception.RegraDeNegocioException;
import com.example.AminalReport.mapper.DenunciaMapper;
import com.example.AminalReport.repository.formularios.DenunciaRepository;
import com.example.AminalReport.repository.usuarios.OrganizacaoRepository;
import com.example.AminalReport.repository.usuarios.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DenunciaService {

    private static final String PASTA_UPLOAD = "denuncia";

    private final DenunciaRepository denunciaRepository;
    private final UserRepository userRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final UploadService uploadService;
    private final DenunciaMapper denunciaMapper;

    public DenunciaService(DenunciaRepository denunciaRepository,
                           UserRepository userRepository,
                           OrganizacaoRepository organizacaoRepository,
                           UploadService uploadService,
                           DenunciaMapper denunciaMapper) {
        this.denunciaRepository = denunciaRepository;
        this.userRepository = userRepository;
        this.organizacaoRepository = organizacaoRepository;
        this.uploadService = uploadService;
        this.denunciaMapper = denunciaMapper;
    }

    /**
     * @param emailCriador e-mail do usuário autenticado; {@code null} para visitante anônimo
     */
    @Transactional
    public void registrar(DenunciaRequest request, String emailCriador) {
        Denuncia denuncia = denunciaMapper.toEntity(request);
        denuncia.setUsuarioCriador(buscarUsuario(emailCriador));
        denuncia.setFoto(uploadService.salvarImagem(request.foto(), PASTA_UPLOAD));
        denuncia.setAndamentoDenuncia(EnumAndamentoDenuncia.AGUARDANDO);

        denunciaRepository.save(denuncia);
    }

    /** Denúncia urgente: sem endereço, sempre com urgência máxima. */
    @Transactional
    public void registrarUrgente(DenunciaUrgenteRequest request, String emailCriador) {
        Denuncia denuncia = denunciaMapper.toEntity(request);
        denuncia.setUsuarioCriador(buscarUsuario(emailCriador));
        denuncia.setFoto(uploadService.salvarImagem(request.foto(), PASTA_UPLOAD));

        denuncia.setBairro(Denuncia.LOCAL_DENUNCIA_URGENTE);
        denuncia.setEstado(Denuncia.LOCAL_DENUNCIA_URGENTE);
        denuncia.setMunicipio(Denuncia.LOCAL_DENUNCIA_URGENTE);
        denuncia.setCep(Denuncia.CEP_DENUNCIA_URGENTE);
        denuncia.setUrgencia(EnumNivelUrgencia.EMERGENCIA);
        denuncia.setAndamentoDenuncia(EnumAndamentoDenuncia.AGUARDANDO);

        denunciaRepository.save(denuncia);
    }

    /** Denúncias que ainda não foram assumidas por nenhuma organização (mais recentes primeiro). */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarPendentes() {
        return denunciaMapper.toResponseList(denunciaRepository.findByOrganizacaoResponsavelIsNullOrderByIdDesc());
    }

    /**
     * Denúncias exibidas na tela de status: as criadas pelo usuário comum
     * ou as sob responsabilidade da organização.
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarParaStatus(String email) {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        List<Denuncia> denuncias;
        if (usuario instanceof Comum) {
            denuncias = denunciaRepository.findByUsuarioCriador(usuario);
        } else if (usuario instanceof Organizacao) {
            denuncias = denunciaRepository.findByOrganizacaoResponsavel(usuario);
        } else {
            denuncias = List.of();
        }
        return denunciaMapper.toResponseList(denuncias);
    }

    @Transactional(readOnly = true)
    public Optional<DenunciaResponse> buscarPorId(Long id) {
        return denunciaRepository.findById(id).map(denunciaMapper::toResponse);
    }

    @Transactional
    public void atualizar(Long id, DenunciaAlteracaoRequest request) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Denúncia não encontrada: " + id));

        denunciaMapper.atualizarEntidade(denuncia, request);
        denuncia.setFoto(uploadService.substituirImagem(denuncia.getFoto(), request.foto(), PASTA_UPLOAD));

        denunciaRepository.save(denuncia);
    }

    /** Uma organização passa a ser a responsável pela denúncia. */
    @Transactional
    public void assumir(Long idDenuncia, String emailOrganizacao) {
        Organizacao organizacao = organizacaoRepository.findByEmail(emailOrganizacao)
                .orElseThrow(() -> new AccessDeniedException("Apenas organizações podem assumir denúncias."));

        Denuncia denuncia = denunciaRepository.findById(idDenuncia)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Denúncia não encontrada: " + idDenuncia));

        if (denuncia.getOrganizacaoResponsavel() != null) {
            throw new RegraDeNegocioException("Esta denúncia já foi assumida por uma organização.");
        }

        denuncia.setOrganizacaoResponsavel(organizacao);
        denunciaRepository.save(denuncia);
    }

    private Usuario buscarUsuario(String email) {
        return email == null ? null : userRepository.findByEmail(email).orElse(null);
    }
}
