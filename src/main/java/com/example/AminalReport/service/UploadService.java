package com.example.AminalReport.service;

import com.example.AminalReport.exception.ArmazenamentoException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadService {

    private final Path raizUploads;

    public UploadService() {
        this(Paths.get("uploads"));
    }

    /** Permite apontar para outro diretório (usado nos testes). */
    UploadService(Path raizUploads) {
        this.raizUploads = raizUploads;
    }

    /**
     * Salva a imagem em {@code uploads/<pasta>/} e devolve o caminho relativo
     * a ser persistido, ou {@code null} se nenhum arquivo foi enviado.
     */
    public String salvarImagem(MultipartFile arquivo, String pasta) {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        String nomeArquivo = UUID.randomUUID() + "_" + nomeSeguro(arquivo.getOriginalFilename());
        Path caminho = raizUploads.resolve(pasta).resolve(nomeArquivo).normalize();

        if (!caminho.startsWith(raizUploads.normalize())) {
            throw new ArmazenamentoException("Caminho de arquivo inválido.", null);
        }

        try {
            Files.createDirectories(caminho.getParent());
            Files.write(caminho, arquivo.getBytes());
        } catch (IOException e) {
            throw new ArmazenamentoException("Erro ao salvar a imagem.", e);
        }

        return pasta + "/" + nomeArquivo;
    }

    public void deletarImagem(String fotoPath) {
        if (fotoPath == null || fotoPath.isBlank()) {
            return;
        }

        Path caminho = raizUploads.resolve(fotoPath).normalize();
        if (!caminho.startsWith(raizUploads.normalize())) {
            return;
        }

        try {
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            throw new ArmazenamentoException("Erro ao remover a imagem.", e);
        }
    }

    /**
     * Substitui a imagem atual por uma nova. Se nenhum arquivo novo foi enviado,
     * mantém a atual. A nova imagem é gravada <b>antes</b> de apagar a antiga,
     * para não perder a foto caso o salvamento falhe.
     *
     * @return o caminho relativo que deve ficar persistido
     */
    public String substituirImagem(String caminhoAtual, MultipartFile novoArquivo, String pasta) {
        if (novoArquivo == null || novoArquivo.isEmpty()) {
            return caminhoAtual;
        }

        String novoCaminho = salvarImagem(novoArquivo, pasta);
        deletarImagem(caminhoAtual);
        return novoCaminho;
    }

    /**
     * Mantém apenas o nome do arquivo (descarta qualquer diretório enviado pelo
     * cliente, como {@code ../../x}) e troca caracteres problemáticos por "_".
     */
    private String nomeSeguro(String nomeOriginal) {
        String nome = nomeOriginal == null
                ? ""
                : StringUtils.getFilename(StringUtils.cleanPath(nomeOriginal));
        return nome == null ? "" : nome.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
