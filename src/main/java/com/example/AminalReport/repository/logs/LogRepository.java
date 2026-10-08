package com.example.AminalReport.repository.logs;

import com.example.AminalReport.entities.logs.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
