package com.example.AminalReport.entities.formularios;

import com.example.AminalReport.entities.enums.EnumAndamentoDenuncia;
import com.example.AminalReport.entities.enums.EnumNivelUrgencia;
import com.example.AminalReport.entities.enums.EnumTipoAnimal;
import com.example.AminalReport.entities.usuarios.Organizacao;
import com.example.AminalReport.entities.usuarios.Usuario;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("DENUNCIA")
public class Denuncia extends Formulario {

    /** Valores fixos usados nas denúncias criadas pelo formulário "urgente" (sem endereço). */
    public static final String CEP_DENUNCIA_URGENTE = "98998-989";
    public static final String LOCAL_DENUNCIA_URGENTE = "URGÊNCIA";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumNivelUrgencia urgencia;

    @JoinColumn(nullable = true)
    @ManyToOne
    private Organizacao organizacaoResponsavel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumAndamentoDenuncia andamentoDenuncia;

    @Column
    private String cep;

    @Column
    private String rua;

    @Column (nullable = false)
    private String bairro;

    @Column (nullable = false)
    private String municipio;

    @Column (nullable = false)
    private String estado;

    @Column (columnDefinition = "TEXT")
    private String pontoRef;
    //Construtores
    public Denuncia () {}


    public Denuncia(String foto, EnumTipoAnimal tipoAnimal, String descricao, Usuario usuarioCriador, String contato, EnumNivelUrgencia urgencia, Organizacao organizacaoResponsavel, EnumAndamentoDenuncia andamentoDenuncia, String cep, String rua, String bairro, String municipio, String estado, String pontoRef) {
        super(foto, tipoAnimal, descricao, usuarioCriador, contato);
        this.urgencia = urgencia;
        this.organizacaoResponsavel = organizacaoResponsavel;
        this.andamentoDenuncia = andamentoDenuncia;
        this.cep = cep;
        this.rua = rua;
        this.bairro = bairro;
        this.municipio = municipio;
        this.estado = estado;
        this.pontoRef = pontoRef;
    }

//Getters e Setters


    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPontoRef() {
        return pontoRef;
    }

    public void setPontoRef(String pontoRef) {
        this.pontoRef = pontoRef;
    }

    public EnumNivelUrgencia getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(EnumNivelUrgencia urgencia) {
        this.urgencia = urgencia;
    }

    public Organizacao getOrganizacaoResponsavel() {
        return organizacaoResponsavel;
    }

    public void setOrganizacaoResponsavel(Organizacao organizacaoResponsavel) {
        this.organizacaoResponsavel = organizacaoResponsavel;
    }

    public EnumAndamentoDenuncia getAndamentoDenuncia() {
        return andamentoDenuncia;
    }

    public void setAndamentoDenuncia(EnumAndamentoDenuncia andamentoDenuncia) {
        this.andamentoDenuncia = andamentoDenuncia;
    }
}
