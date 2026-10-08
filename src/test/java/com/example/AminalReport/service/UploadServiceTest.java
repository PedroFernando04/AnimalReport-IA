package com.example.AminalReport.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UploadServiceTest {

    @Test
    void ignoraArquivoVazioOuNulo() throws IOException {
        UploadService service = new UploadService(Files.createTempDirectory("uploads"));

        assertNull(service.salvarImagem(null, "adocao"));
        assertNull(service.salvarImagem(new MockMultipartFile("foto", new byte[0]), "adocao"));
    }

    @Test
    void salvaDentroDaPastaEDescartaDiretoriosDoNomeOriginal() throws IOException {
        Path raiz = Files.createTempDirectory("uploads");
        UploadService service = new UploadService(raiz);
        var arquivo = new MockMultipartFile("foto", "../../etc/evil foto.png", "image/png", new byte[]{1, 2, 3});

        String caminhoRelativo = service.salvarImagem(arquivo, "adocao");

        assertTrue(caminhoRelativo.startsWith("adocao/"));
        assertFalse(caminhoRelativo.contains(".."));
        assertFalse(caminhoRelativo.contains(" "));
        assertTrue(caminhoRelativo.endsWith("_evil_foto.png"));
        assertTrue(Files.exists(raiz.resolve(caminhoRelativo)));
    }

    @Test
    void substituirImagemMantemAtualQuandoNaoHaNovoArquivo() throws IOException {
        UploadService service = new UploadService(Files.createTempDirectory("uploads"));

        assertEquals("adocao/atual.png", service.substituirImagem("adocao/atual.png", null, "adocao"));
        assertEquals("adocao/atual.png",
                service.substituirImagem("adocao/atual.png", new MockMultipartFile("foto", new byte[0]), "adocao"));
    }

    @Test
    void substituirImagemGravaNovaEApagaAntiga() throws IOException {
        Path raiz = Files.createTempDirectory("uploads");
        UploadService service = new UploadService(raiz);

        String antiga = service.salvarImagem(new MockMultipartFile("foto", "a.png", "image/png", new byte[]{1}), "adocao");
        String nova = service.substituirImagem(antiga,
                new MockMultipartFile("foto", "b.png", "image/png", new byte[]{2}), "adocao");

        assertFalse(Files.exists(raiz.resolve(antiga)));
        assertTrue(Files.exists(raiz.resolve(nova)));
    }

    @Test
    void deletarImagemNaoSaiDaPastaDeUploads() throws IOException {
        Path base = Files.createTempDirectory("base");
        Path raiz = Files.createDirectory(base.resolve("uploads"));
        Path forasteiro = Files.writeString(base.resolve("segredo.txt"), "x");
        UploadService service = new UploadService(raiz);

        service.deletarImagem("../segredo.txt");

        assertTrue(Files.exists(forasteiro));
    }
}
