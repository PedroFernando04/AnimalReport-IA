package com.example.AminalReport.entities.enums;

public enum EnumTipoAnimal {

    CACHORRO("Cachorro"),
    GATO("Gato"),
    PASSARO("Pássaro"), // Note o acento, agora pode!
    CAVALO("Cavalo"),
    BOI("Boi"),
    VACA("Vaca"),
    PORCO("Porco"),
    CABRA("Cabra"),
    OVELHA("Ovelha"),
    GALINHA("Galinha"),
    PATO("Pato"),
    PEIXE("Peixe"),
    COELHO("Coelho"),
    TARTARUGA("Tartaruga"),
    ANIMAL_SILVESTRE("Animal Silvestre"), // Remove o underline na visualização
    OUTRO("Outro");

    private final String descricao;

    EnumTipoAnimal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}