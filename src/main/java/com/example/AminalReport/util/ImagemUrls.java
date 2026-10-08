package com.example.AminalReport.util;

/**
 * Centraliza a montagem das URLs de imagens exibidas nas páginas.
 * Antes essa lógica estava duplicada em vários controllers.
 */
public final class ImagemUrls {

    public static final String PREFIXO_UPLOADS = "/uploads/";
    public static final String IMAGEM_PADRAO_ANIMAL = "/images/sem-foto.jpg";
    public static final String IMAGEM_PADRAO_PERFIL = "/images/perfilPadrao.jpg";

    private ImagemUrls() {
    }

    public static String doAnimal(String fotoPath) {
        return montar(fotoPath, IMAGEM_PADRAO_ANIMAL);
    }

    public static String doPerfil(String fotoPath) {
        return montar(fotoPath, IMAGEM_PADRAO_PERFIL);
    }

    private static String montar(String fotoPath, String padrao) {
        return (fotoPath != null && !fotoPath.isBlank())
                ? PREFIXO_UPLOADS + fotoPath
                : padrao;
    }
}
