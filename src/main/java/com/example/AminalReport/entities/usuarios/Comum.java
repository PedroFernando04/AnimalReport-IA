package com.example.AminalReport.entities.usuarios;

import com.example.AminalReport.entities.enums.EnumStatusUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("COMUM")
public class Comum extends Usuario{
    @Column(nullable = false, length = 11)
    private String cpf;

    //Construtores

    public Comum () {}

    public Comum(String nome, String email, String telefone, String senha, LocalDateTime dataCadastro, EnumStatusUsuario statusUsuario, String foto, String cpf) {
        super(nome, email, telefone, senha, dataCadastro, statusUsuario, foto);
        this.cpf = cpf;
    }

    //Getter e Setters


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf = cpf.replaceAll("\\D", "");;
    }

    public String getCpfFormatado() {

        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }

        return cpf.replaceFirst(
                "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4"
        );

    }
}
