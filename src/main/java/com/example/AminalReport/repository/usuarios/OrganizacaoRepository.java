package com.example.AminalReport.repository.usuarios;

import com.example.AminalReport.entities.usuarios.Organizacao;
import com.example.AminalReport.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

    Organizacao findByCnpj (String cnpj);

    Organizacao findByInscricaoEstadual (String inscricaoEstadual);

    Optional<Organizacao> findByEmail(String email);
}
