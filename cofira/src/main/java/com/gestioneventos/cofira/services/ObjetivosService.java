package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.entities.Objetivos;
import com.gestioneventos.cofira.repositories.ObjetivosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjetivosService {
    private static final String OBJETIVOS_NO_ENCONTRADOS = "Objetivos no encontrados con id ";

    private final ObjetivosRepository objetivosRepository;

    public ObjetivosService(ObjetivosRepository objetivosRepository) {
        this.objetivosRepository = objetivosRepository;
    }

    public List<Objetivos> listarObjetivos() {
        return objetivosRepository.findAll();
    }

    public Objetivos obtenerObjetivos(Long id) {
        return objetivosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(OBJETIVOS_NO_ENCONTRADOS + id));
    }

    public Objetivos obtenerObjetivosPorUsuario(Long usuarioId) {
        return objetivosRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Objetivos no encontrados para usuario con id " + usuarioId));
    }

    public Objetivos crearObjetivos(Objetivos objetivos) {
        return objetivosRepository.save(objetivos);
    }

    public Objetivos actualizarObjetivos(Long id, Objetivos objetivosActualizados) {
        Objetivos objetivos = objetivosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(OBJETIVOS_NO_ENCONTRADOS + id));

        if (objetivosActualizados.getListaObjetivos() != null) {
            objetivos.setListaObjetivos(objetivosActualizados.getListaObjetivos());
        }

        return objetivosRepository.save(objetivos);
    }

    public void eliminarObjetivos(Long id) {
        Objetivos objetivos = objetivosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(OBJETIVOS_NO_ENCONTRADOS + id));
        objetivosRepository.delete(objetivos);
    }
}
