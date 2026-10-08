package com.example.AminalReport.entities.usuarios;

import com.example.AminalReport.entities.enums.EnumStatusUsuario;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity(name = "Usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column
    private String telefone;

    @Column(nullable = false)
    private String senha;

    @Column
    private LocalDateTime dataCadastro;


    @Column
    @Enumerated(EnumType.STRING)
    private EnumStatusUsuario statusUsuario;

    @Column
    private String fotoPath;

    //Construtores

    public Usuario(){}

    public Usuario(String nome, String email, String telefone, String senha, LocalDateTime dataCadastro, EnumStatusUsuario statusUsuario, String fotoPath) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
        this.statusUsuario = statusUsuario;
        this.fotoPath = fotoPath;
    }

    //Getters e Setters


    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getTelefoneFormatado() {

        if (telefone == null || telefone.length() != 11) {
            return telefone;
        }

        return telefone.replaceFirst(
                "(\\d{2})(\\d{5})(\\d{4})",
                "($1) $2-$3"
        );

    }

    public void setTelefone(String telefone) {
        this.telefone = telefone.replaceAll("\\D", "");
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public EnumStatusUsuario getStatusUsuario() {
        return statusUsuario;
    }

    public void setStatusUsuario(EnumStatusUsuario statusUsuario) {
        this.statusUsuario = statusUsuario;
    }

    public String getFoto() {
        return fotoPath;
    }

    public void setFoto(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getTipoUsuario() {return this.getClass().getSimpleName();}
}
