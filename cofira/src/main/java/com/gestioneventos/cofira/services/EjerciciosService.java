package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.entities.Ejercicios;
import com.gestioneventos.cofira.repositories.EjerciciosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjerciciosService {
    private static final String EJERCICIO_NO_ENCONTRADO = "Ejercicio no encontrado con id ";

    private final EjerciciosRepository ejerciciosRepository;

    public EjerciciosService(EjerciciosRepository ejerciciosRepository) {
        this.ejerciciosRepository = ejerciciosRepository;
    }

    public List<Ejercicios> listarEjercicios() {
        return ejerciciosRepository.findAll();
    }

    public Ejercicios obtenerEjercicio(Long id) {
        return ejerciciosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EJERCICIO_NO_ENCONTRADO + id));
    }

    public List<Ejercicios> obtenerEjerciciosPorSala(Long salaId) {
        return ejerciciosRepository.findBySalaDeGimnasioId(salaId);
    }

    public Ejercicios crearEjercicio(Ejercicios ejercicio) {
        return ejerciciosRepository.save(ejercicio);
    }

    public Ejercicios actualizarEjercicio(Long id, Ejercicios ejercicioActualizado) {
        Ejercicios ejercicio = ejerciciosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EJERCICIO_NO_ENCONTRADO + id));

        if (ejercicioActualizado.getNombreEjercicio() != null) {
            ejercicio.setNombreEjercicio(ejercicioActualizado.getNombreEjercicio());
        }
        if (ejercicioActualizado.getSeries() != null) {
            ejercicio.setSeries(ejercicioActualizado.getSeries());
        }
        if (ejercicioActualizado.getRepeticiones() != null) {
            ejercicio.setRepeticiones(ejercicioActualizado.getRepeticiones());
        }

        return ejerciciosRepository.save(ejercicio);
    }

    public void eliminarEjercicio(Long id) {
        Ejercicios ejercicio = ejerciciosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EJERCICIO_NO_ENCONTRADO + id));
        ejerciciosRepository.delete(ejercicio);
    }
}
