package com.example.AminalReport.repository.usuarios;

import com.example.AminalReport.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository <Usuario,Long>{

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

}
