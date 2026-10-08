package com.example.AminalReport.repository.formularios;

import com.example.AminalReport.entities.formularios.Denuncia;
import com.example.AminalReport.entities.usuarios.Organizacao;
import com.example.AminalReport.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    List<Denuncia> findByUsuarioCriador(Usuario user);

    List<Denuncia> findByOrganizacaoResponsavel(Usuario org);

    List<Denuncia> findByOrganizacaoResponsavelIsNullOrderByIdDesc();

}