package com.example.AminalReport.entities.formularios;

import com.example.AminalReport.entities.enums.EnumAndamentoAdocao;
import com.example.AminalReport.entities.usuarios.Usuario;
import com.example.AminalReport.entities.enums.EnumTipoAnimal;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADOCAO")
public class Adocao extends Formulario{

    @Column
    private String nomeAnimal;

    @Column
    private Integer idadeEstimada;

    @Column
    @Enumerated(EnumType.STRING)
    private EnumAndamentoAdocao statusAdocao;

    //Construtores
    public Adocao () {}


    public Adocao(String foto, EnumTipoAnimal tipoAnimal, String descricao, Usuario usuarioCriador, String contato, String nomeAnimal, Integer idadeEstimada,  EnumAndamentoAdocao statusAdocao) {
        super(foto, tipoAnimal, descricao, usuarioCriador, contato);
        this.nomeAnimal = nomeAnimal;
        this.idadeEstimada = idadeEstimada;
        this.statusAdocao = statusAdocao;
    }

    //Getters e Setters

    public String getNomeAnimal() {
        return nomeAnimal;
    }

    public void setNomeAnimal(String nomeAnimal) {
        this.nomeAnimal = nomeAnimal;
    }

    public Integer getIdadeEstimada() {
        return idadeEstimada;
    }

    public void setIdadeEstimada(Integer idadeEstimada) {
        this.idadeEstimada = idadeEstimada;
    }

    public EnumAndamentoAdocao getStatusAdocao() {return statusAdocao;}

    public void setStatusAdocao(EnumAndamentoAdocao statusAdocao) {this.statusAdocao = statusAdocao;}
}
