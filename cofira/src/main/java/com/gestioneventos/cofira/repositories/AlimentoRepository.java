package com.gestioneventos.cofira.repositories;

import com.gestioneventos.cofira.entities.Alimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlimentoRepository extends JpaRepository<Alimento, Long> {
}
