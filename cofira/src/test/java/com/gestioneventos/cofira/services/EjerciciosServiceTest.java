package com.gestioneventos.cofira.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gestioneventos.cofira.dto.ejercicios.CrearEjerciciosDTO;
import com.gestioneventos.cofira.dto.ejercicios.EjerciciosDTO;
import com.gestioneventos.cofira.dto.ejercicios.ModificarEjerciciosDTO;
import com.gestioneventos.cofira.entities.Ejercicios;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.repositories.EjerciciosRepository;

public class EjerciciosServiceTest {

    @Mock
    private EjerciciosRepository ejerciciosRepository;

    @InjectMocks
    private EjerciciosService ejerciciosService;

    private Ejercicios ejercicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ejercicio = new Ejercicios();
        ejercicio.setId(1L);
        ejercicio.setNombreEjercicio("Flexiones");
        ejercicio.setSeries(3);
        ejercicio.setRepeticiones(10);
        ejercicio.setTiempoDescansoSegundos(60);
        ejercicio.setDescripcion("Ejercicio de pecho");
        ejercicio.setGrupoMuscular("Pecho");
    }

    @Test
    void testListarEjercicios_NotEmpty() {
        when(ejerciciosRepository.findAll()).thenReturn(Collections.singletonList(ejercicio));

        List<EjerciciosDTO> ejercicios = ejerciciosService.listarEjercicios();

        assertNotNull(ejercicios);
        assertEquals(1, ejercicios.size());
        assertEquals("Flexiones", ejercicios.get(0).getNombreEjercicio());
        verify(ejerciciosRepository, times(1)).findAll();
    }

    @Test
    void testListarEjercicios_Empty() {
        when(ejerciciosRepository.findAll()).thenReturn(Collections.emptyList());

        List<EjerciciosDTO> ejercicios = ejerciciosService.listarEjercicios();

        assertNotNull(ejercicios);
        assertTrue(ejercicios.isEmpty());
        verify(ejerciciosRepository, times(1)).findAll();
    }

    @Test
    void testObtenerEjercicio_Success() {
        when(ejerciciosRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        EjerciciosDTO foundEjercicio = ejerciciosService.obtenerEjercicio(1L);

        assertNotNull(foundEjercicio);
        assertEquals(1L, foundEjercicio.getId());
        assertEquals("Flexiones", foundEjercicio.getNombreEjercicio());
        verify(ejerciciosRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerEjercicio_NotFound() {
        when(ejerciciosRepository.findById(2L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            ejerciciosService.obtenerEjercicio(2L);
        });

        assertEquals("Ejercicio no encontrado con id 2", exception.getMessage());
        verify(ejerciciosRepository, times(1)).findById(2L);
    }

    @Test
    void testCrearEjercicio_Success() {
        CrearEjerciciosDTO crearDTO = new CrearEjerciciosDTO();
        crearDTO.setNombreEjercicio("Sentadillas");
        crearDTO.setSeries(4);
        crearDTO.setRepeticiones(12);
        crearDTO.setTiempoDescansoSegundos(90);
        crearDTO.setDescripcion("Ejercicio de piernas");
        crearDTO.setGrupoMuscular("Piernas");

        Ejercicios newEjercicio = new Ejercicios();
        newEjercicio.setId(2L);
        newEjercicio.setNombreEjercicio(crearDTO.getNombreEjercicio());
        newEjercicio.setSeries(crearDTO.getSeries());
        newEjercicio.setRepeticiones(crearDTO.getRepeticiones());
        newEjercicio.setTiempoDescansoSegundos(crearDTO.getTiempoDescansoSegundos());
        newEjercicio.setDescripcion(crearDTO.getDescripcion());
        newEjercicio.setGrupoMuscular(crearDTO.getGrupoMuscular());

        when(ejerciciosRepository.save(any(Ejercicios.class))).thenReturn(newEjercicio);

        EjerciciosDTO createdEjercicio = ejerciciosService.crearEjercicio(crearDTO);

        assertNotNull(createdEjercicio);
        assertEquals(2L, createdEjercicio.getId());
        assertEquals("Sentadillas", createdEjercicio.getNombreEjercicio());
        verify(ejerciciosRepository, times(1)).save(any(Ejercicios.class));
    }

    @Test
    void testActualizarEjercicio_Success() {
        ModificarEjerciciosDTO modificarDTO = new ModificarEjerciciosDTO();
        modificarDTO.setNombreEjercicio("Sentadillas Modificadas");
        modificarDTO.setSeries(5);
        modificarDTO.setRepeticiones(15);
        modificarDTO.setTiempoDescansoSegundos(120);
        modificarDTO.setDescripcion("Ejercicio de piernas avanzado");
        modificarDTO.setGrupoMuscular("Piernas");

        Ejercicios updatedEjercicio = new Ejercicios();
        updatedEjercicio.setId(1L);
        updatedEjercicio.setNombreEjercicio(modificarDTO.getNombreEjercicio());
        updatedEjercicio.setSeries(modificarDTO.getSeries());
        updatedEjercicio.setRepeticiones(modificarDTO.getRepeticiones());
        updatedEjercicio.setTiempoDescansoSegundos(modificarDTO.getTiempoDescansoSegundos());
        updatedEjercicio.setDescripcion(modificarDTO.getDescripcion());
        updatedEjercicio.setGrupoMuscular(modificarDTO.getGrupoMuscular());

        when(ejerciciosRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(ejerciciosRepository.save(any(Ejercicios.class))).thenReturn(updatedEjercicio);

        EjerciciosDTO result = ejerciciosService.actualizarEjercicio(1L, modificarDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sentadillas Modificadas", result.getNombreEjercicio());
        assertEquals(5, result.getSeries());
        verify(ejerciciosRepository, times(1)).findById(1L);
        verify(ejerciciosRepository, times(1)).save(any(Ejercicios.class));
    }

    @Test
    void testActualizarEjercicio_PartialUpdate() {
        ModificarEjerciciosDTO modificarDTO = new ModificarEjerciciosDTO();
        modificarDTO.setNombreEjercicio("Flexiones Avanzadas"); // Only update name

        Ejercicios existingEjercicio = new Ejercicios();
        existingEjercicio.setId(1L);
        existingEjercicio.setNombreEjercicio("Flexiones");
        existingEjercicio.setSeries(3);
        existingEjercicio.setRepeticiones(10);

        Ejercicios updatedEjercicio = new Ejercicios();
        updatedEjercicio.setId(1L);
        updatedEjercicio.setNombreEjercicio("Flexiones Avanzadas"); // Updated
        updatedEjercicio.setSeries(3); // Unchanged
        updatedEjercicio.setRepeticiones(10); // Unchanged

        when(ejerciciosRepository.findById(1L)).thenReturn(Optional.of(existingEjercicio));
        when(ejerciciosRepository.save(any(Ejercicios.class))).thenReturn(updatedEjercicio);

        EjerciciosDTO result = ejerciciosService.actualizarEjercicio(1L, modificarDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Flexiones Avanzadas", result.getNombreEjercicio());
        assertEquals(3, result.getSeries()); // Ensure other fields are unchanged
        verify(ejerciciosRepository, times(1)).findById(1L);
        verify(ejerciciosRepository, times(1)).save(any(Ejercicios.class));
    }

    @Test
    void testActualizarEjercicio_NotFound() {
        ModificarEjerciciosDTO modificarDTO = new ModificarEjerciciosDTO();
        modificarDTO.setNombreEjercicio("No Existe");

        when(ejerciciosRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            ejerciciosService.actualizarEjercicio(99L, modificarDTO);
        });

        assertEquals("Ejercicio no encontrado con id 99", exception.getMessage());
        verify(ejerciciosRepository, times(1)).findById(99L);
        verify(ejerciciosRepository, never()).save(any(Ejercicios.class));
    }

    @Test
    void testEliminarEjercicio_Success() {
        when(ejerciciosRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        doNothing().when(ejerciciosRepository).delete(ejercicio);

        ejerciciosService.eliminarEjercicio(1L);

        verify(ejerciciosRepository, times(1)).findById(1L);
        verify(ejerciciosRepository, times(1)).delete(ejercicio);
    }

    @Test
    void testEliminarEjercicio_NotFound() {
        when(ejerciciosRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            ejerciciosService.eliminarEjercicio(99L);
        });

        assertEquals("Ejercicio no encontrado con id 99", exception.getMessage());
        verify(ejerciciosRepository, times(1)).findById(99L);
        verify(ejerciciosRepository, never()).delete(any(Ejercicios.class));
    }


}
