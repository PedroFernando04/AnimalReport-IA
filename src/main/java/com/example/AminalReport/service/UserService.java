package com.example.AminalReport.service;

import com.example.AminalReport.dto.request.RegistroComumRequest;
import com.example.AminalReport.dto.request.RegistroOrganizacaoRequest;
import com.example.AminalReport.dto.request.UsuarioAlteracaoRequest;
import com.example.AminalReport.dto.response.UsuarioLogadoResponse;
import com.example.AminalReport.entities.enums.EnumStatusUsuario;
import com.example.AminalReport.entities.usuarios.Comum;
import com.example.AminalReport.entities.usuarios.Organizacao;
import com.example.AminalReport.entities.usuarios.Usuario;
import com.example.AminalReport.exception.RecursoNaoEncontradoException;
import com.example.AminalReport.exception.RegraDeNegocioException;
import com.example.AminalReport.mapper.UsuarioMapper;
import com.example.AminalReport.repository.usuarios.ComumRepository;
import com.example.AminalReport.repository.usuarios.OrganizacaoRepository;
import com.example.AminalReport.repository.usuarios.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private static final String PASTA_UPLOAD = "usuario";

    private final UserRepository userRepository;
    private final ComumRepository comumRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    private final UsuarioMapper usuarioMapper;

    public UserService(UserRepository userRepository,
                       ComumRepository comumRepository,
                       OrganizacaoRepository organizacaoRepository,
                       PasswordEncoder passwordEncoder,
                       UploadService uploadService,
                       UsuarioMapper usuarioMapper) {
        this.userRepository = userRepository;
        this.comumRepository = comumRepository;
        this.organizacaoRepository = organizacaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public void registrarComum(RegistroComumRequest request) {
        garantirEmailDisponivel(request.email());

        Comum comum = usuarioMapper.toEntity(request);
        if (comumRepository.findByCpf(comum.getCpf()) != null) {
            throw new RegraDeNegocioException("CPF já cadastrado.");
        }

        prepararNovoUsuario(comum, request.senha());
        userRepository.save(comum);
    }

    @Transactional
    public void registrarOrganizacao(RegistroOrganizacaoRequest request) {
        garantirEmailDisponivel(request.email());

        Organizacao organizacao = usuarioMapper.toEntity(request);
        if (organizacaoRepository.findByCnpj(organizacao.getCnpj()) != null) {
            throw new RegraDeNegocioException("CNPJ já cadastrado.");
        }

        prepararNovoUsuario(organizacao, request.senha());
        userRepository.save(organizacao);
    }

    /** Dados do usuário autenticado para as views; vazio se o e-mail não existir mais. */
    @Transactional(readOnly = true)
    public Optional<UsuarioLogadoResponse> buscarLogado(String email) {
        return userRepository.findByEmail(email).map(usuarioMapper::toLogadoResponse);
    }

    /**
     * Atualiza o perfil. Um usuário só pode alterar a própria conta.
     *
     * @param emailAutenticado e-mail do usuário que fez a requisição
     */
    @Transactional
    public void alterar(Long id, UsuarioAlteracaoRequest request, String emailAutenticado) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));

        if (!usuario.getEmail().equals(emailAutenticado)) {
            throw new AccessDeniedException("Você só pode alterar o seu próprio perfil.");
        }

        if (request.email() != null
                && !request.email().equals(usuario.getEmail())) {
            garantirEmailDisponivel(request.email());
        }

        usuarioMapper.atualizarEntidade(usuario, request);

        if (request.senha() != null) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        usuario.setFoto(uploadService.substituirImagem(usuario.getFoto(), request.foto(), PASTA_UPLOAD));

        userRepository.save(usuario);
    }

    private void garantirEmailDisponivel(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RegraDeNegocioException("E-mail já cadastrado.");
        }
    }

    private void prepararNovoUsuario(Usuario usuario, String senhaPura) {
        usuario.setSenha(passwordEncoder.encode(senhaPura));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setStatusUsuario(EnumStatusUsuario.ATIVO);
        usuario.setFoto(null);
    }
}
