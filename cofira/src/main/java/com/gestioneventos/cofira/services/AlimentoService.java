package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.entities.Alimento;
import com.gestioneventos.cofira.repositories.AlimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlimentoService {
    private static final String ALIMENTO_NO_ENCONTRADO = "Alimento no encontrado con id ";

    private final AlimentoRepository alimentoRepository;

    public AlimentoService(AlimentoRepository alimentoRepository) {
        this.alimentoRepository = alimentoRepository;
    }

    public List<Alimento> listarAlimentos() {
        return alimentoRepository.findAll();
    }

    public Alimento obtenerAlimento(Long id) {
        return alimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ALIMENTO_NO_ENCONTRADO + id));
    }

    public Alimento crearAlimento(Alimento alimento) {
        return alimentoRepository.save(alimento);
    }

    public Alimento actualizarAlimento(Long id, Alimento alimentoActualizado) {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ALIMENTO_NO_ENCONTRADO + id));

        if (alimentoActualizado.getAlimentosFavoritos() != null) {
            alimento.setAlimentosFavoritos(alimentoActualizado.getAlimentosFavoritos());
        }
        if (alimentoActualizado.getListaAlergias() != null) {
            alimento.setListaAlergias(alimentoActualizado.getListaAlergias());
        }

        return alimentoRepository.save(alimento);
    }

    public void eliminarAlimento(Long id) {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ALIMENTO_NO_ENCONTRADO + id));
        alimentoRepository.delete(alimento);
    }
}
