package com.gestioneventos.cofira.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gestioneventos.cofira.dto.alimento.AlimentoDTO;
import com.gestioneventos.cofira.entities.Alimento;
import com.gestioneventos.cofira.repositories.AlimentoRepository;

public class AlimentoServiceTest {

    @Mock
    private AlimentoRepository alimentoRepository;

    @InjectMocks
    private AlimentoService alimentoService;

    private Alimento alimento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alimento = new Alimento();
        alimento.setId(1L);
        alimento.setNombre("Manzana");
        alimento.setIngredientes(Collections.singletonList("Manzana"));
    }

    @Test
    void testListarAlimentos() {
        when(alimentoRepository.findAll()).thenReturn(Collections.singletonList(alimento));

        List<AlimentoDTO> alimentos = alimentoService.listarAlimentos(null);

        assertEquals(1, alimentos.size());
        assertEquals("Manzana", alimentos.get(0).getNombre());
    }
}