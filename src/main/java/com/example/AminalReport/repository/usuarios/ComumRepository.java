package com.example.AminalReport.repository.usuarios;

import com.example.AminalReport.entities.usuarios.Comum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComumRepository extends JpaRepository <Comum, Long> {

    Comum findByCpf (String cpf);
}
