package com.example.AminalReport.repository.formularios;

import com.example.AminalReport.entities.formularios.Adocao;
import com.example.AminalReport.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdocaoRepository extends JpaRepository<Adocao, Long> {
    Adocao findByNomeAnimal (String nome);

    Optional<Adocao> findByNomeAnimalAndUsuarioCriador(String nome, Usuario usuarioCriador);

    List<Adocao> findAllByOrderByIdDesc();

    List<Adocao> findByUsuarioCriador(Usuario usuarioCriador);

    List<Adocao> findTop5ByOrderByIdDesc();
}
