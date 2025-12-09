package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.entities.SalaDeGimnasio;
import com.gestioneventos.cofira.repositories.SalaDeGimnasioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaDeGimnasioService {
    private static final String SALA_NO_ENCONTRADA = "Sala de gimnasio no encontrada con id ";

    private final SalaDeGimnasioRepository salaDeGimnasioRepository;

    public SalaDeGimnasioService(SalaDeGimnasioRepository salaDeGimnasioRepository) {
        this.salaDeGimnasioRepository = salaDeGimnasioRepository;
    }

    public List<SalaDeGimnasio> listarSalas() {
        return salaDeGimnasioRepository.findAll();
    }

    public SalaDeGimnasio obtenerSala(Long id) {
        return salaDeGimnasioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(SALA_NO_ENCONTRADA + id));
    }

    public SalaDeGimnasio crearSala(SalaDeGimnasio sala) {
        return salaDeGimnasioRepository.save(sala);
    }

    public SalaDeGimnasio actualizarSala(Long id, SalaDeGimnasio salaActualizada) {
        SalaDeGimnasio sala = salaDeGimnasioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(SALA_NO_ENCONTRADA + id));

        if (salaActualizada.getFechaInicio() != null) {
            sala.setFechaInicio(salaActualizada.getFechaInicio());
        }
        if (salaActualizada.getFechaFin() != null) {
            sala.setFechaFin(salaActualizada.getFechaFin());
        }

        return salaDeGimnasioRepository.save(sala);
    }

    public void eliminarSala(Long id) {
        SalaDeGimnasio sala = salaDeGimnasioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(SALA_NO_ENCONTRADA + id));
        salaDeGimnasioRepository.delete(sala);
    }
}
