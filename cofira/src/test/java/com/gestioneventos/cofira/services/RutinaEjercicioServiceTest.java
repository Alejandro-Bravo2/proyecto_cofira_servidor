package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.dto.ejercicios.EjerciciosDTO;
import com.gestioneventos.cofira.dto.rutinaejercicio.*;
import com.gestioneventos.cofira.entities.DiaEjercicio;
import com.gestioneventos.cofira.entities.Ejercicios;
import com.gestioneventos.cofira.entities.RutinaEjercicio;
import com.gestioneventos.cofira.enums.DiaSemana;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.repositories.EjerciciosRepository;
import com.gestioneventos.cofira.repositories.RutinaEjercicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RutinaEjercicioServiceTest {

    @Mock
    private RutinaEjercicioRepository rutinaEjercicioRepository;

    @Mock
    private EjerciciosRepository ejerciciosRepository;

    @InjectMocks
    private RutinaEjercicioService rutinaEjercicioService;

    private RutinaEjercicio rutinaEjercicio;
    private Ejercicios ejercicio1;
    private Ejercicios ejercicio2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ejercicio1 = new Ejercicios();
        ejercicio1.setId(1L);
        ejercicio1.setNombreEjercicio("Flexiones");
        ejercicio1.setSeries(3);
        ejercicio1.setRepeticiones(10);
        ejercicio1.setTiempoDescansoSegundos(60);
        ejercicio1.setDescripcion("Ejercicio de pecho");
        ejercicio1.setGrupoMuscular("Pecho");

        ejercicio2 = new Ejercicios();
        ejercicio2.setId(2L);
        ejercicio2.setNombreEjercicio("Sentadillas");
        ejercicio2.setSeries(4);
        ejercicio2.setRepeticiones(12);
        ejercicio2.setTiempoDescansoSegundos(90);
        ejercicio2.setDescripcion("Ejercicio de piernas");
        ejercicio2.setGrupoMuscular("Piernas");

        DiaEjercicio diaLunes = new DiaEjercicio();
        diaLunes.setId(100L);
        diaLunes.setDiaSemana(DiaSemana.LUNES);
        diaLunes.setEjercicios(Arrays.asList(ejercicio1, ejercicio2));

        rutinaEjercicio = new RutinaEjercicio();
        rutinaEjercicio.setId(1L);
        rutinaEjercicio.setFechaInicio(LocalDate.of(2023, 1, 1));
        rutinaEjercicio.setDiasEjercicio(Collections.singletonList(diaLunes));
    }

    @Test
    void testListarRutinas_NotEmpty() {
        when(rutinaEjercicioRepository.findAll()).thenReturn(Collections.singletonList(rutinaEjercicio));

        List<RutinaEjercicioDTO> result = rutinaEjercicioService.listarRutinas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(LocalDate.of(2023, 1, 1), result.get(0).getFechaInicio());
        assertFalse(result.get(0).getDiasEjercicio().isEmpty());
        assertEquals("LUNES", result.get(0).getDiasEjercicio().get(0).getDiaSemana());
        assertEquals(2, result.get(0).getDiasEjercicio().get(0).getEjercicios().size());
        assertEquals("Flexiones", result.get(0).getDiasEjercicio().get(0).getEjercicios().get(0).getNombreEjercicio());
        verify(rutinaEjercicioRepository, times(1)).findAll();
    }

    @Test
    void testListarRutinas_Empty() {
        when(rutinaEjercicioRepository.findAll()).thenReturn(Collections.emptyList());

        List<RutinaEjercicioDTO> result = rutinaEjercicioService.listarRutinas();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rutinaEjercicioRepository, times(1)).findAll();
    }

    @Test
    void testObtenerRutina_Success() {
        when(rutinaEjercicioRepository.findById(1L)).thenReturn(Optional.of(rutinaEjercicio));

        RutinaEjercicioDTO result = rutinaEjercicioService.obtenerRutina(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LocalDate.of(2023, 1, 1), result.getFechaInicio());
        assertFalse(result.getDiasEjercicio().isEmpty());
        assertEquals("LUNES", result.getDiasEjercicio().get(0).getDiaSemana());
        assertEquals(2, result.getDiasEjercicio().get(0).getEjercicios().size());
        assertEquals("Flexiones", result.getDiasEjercicio().get(0).getEjercicios().get(0).getNombreEjercicio());
        verify(rutinaEjercicioRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerRutina_NotFound() {
        when(rutinaEjercicioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            rutinaEjercicioService.obtenerRutina(99L);
        });

        assertEquals("Rutina de ejercicio no encontrada con id 99", exception.getMessage());
        verify(rutinaEjercicioRepository, times(1)).findById(99L);
    }

    @Test
    void testCrearRutina_SuccessWithExercises() {
        CrearDiaEjercicioDTO lunesDTO = new CrearDiaEjercicioDTO();
        lunesDTO.setDiaSemana("LUNES");
        lunesDTO.setEjerciciosIds(Arrays.asList(1L, 2L));

        CrearDiaEjercicioDTO martesDTO = new CrearDiaEjercicioDTO();
        martesDTO.setDiaSemana("MARTES");
        martesDTO.setEjerciciosIds(Collections.singletonList(1L));

        CrearRutinaEjercicioDTO crearRutinaDTO = new CrearRutinaEjercicioDTO();
        crearRutinaDTO.setFechaInicio(LocalDate.of(2023, 10, 26));
        crearRutinaDTO.setDiasEjercicio(Arrays.asList(lunesDTO, martesDTO));

        RutinaEjercicio savedRutina = new RutinaEjercicio();
        savedRutina.setId(2L);
        savedRutina.setFechaInicio(LocalDate.of(2023, 10, 26));
        
        DiaEjercicio savedLunes = new DiaEjercicio();
        savedLunes.setId(200L);
        savedLunes.setDiaSemana(DiaSemana.LUNES);
        savedLunes.setEjercicios(Arrays.asList(ejercicio1, ejercicio2));

        DiaEjercicio savedMartes = new DiaEjercicio();
        savedMartes.setId(201L);
        savedMartes.setDiaSemana(DiaSemana.MARTES);
        savedMartes.setEjercicios(Collections.singletonList(ejercicio1));
        
        savedRutina.setDiasEjercicio(Arrays.asList(savedLunes, savedMartes));


        when(ejerciciosRepository.findById(1L)).thenReturn(Optional.of(ejercicio1));
        when(ejerciciosRepository.findById(2L)).thenReturn(Optional.of(ejercicio2));
        when(rutinaEjercicioRepository.save(any(RutinaEjercicio.class))).thenReturn(savedRutina);

        RutinaEjercicioDTO result = rutinaEjercicioService.crearRutina(crearRutinaDTO);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(LocalDate.of(2023, 10, 26), result.getFechaInicio());
        assertEquals(2, result.getDiasEjercicio().size());
        assertEquals("LUNES", result.getDiasEjercicio().get(0).getDiaSemana());
        assertEquals(2, result.getDiasEjercicio().get(0).getEjercicios().size());
        assertEquals("MARTES", result.getDiasEjercicio().get(1).getDiaSemana());
        assertEquals(1, result.getDiasEjercicio().get(1).getEjercicios().size());
        verify(ejerciciosRepository, times(2)).findById(anyLong()); // 1L and 2L for Monday, 1L for Tuesday
        verify(rutinaEjercicioRepository, times(1)).save(any(RutinaEjercicio.class));
    }

    @Test
    void testCrearRutina_EmptyExerciseListForDay() {
        CrearDiaEjercicioDTO lunesDTO = new CrearDiaEjercicioDTO();
        lunesDTO.setDiaSemana("LUNES");
        lunesDTO.setEjerciciosIds(Collections.emptyList()); // Empty list

        CrearRutinaEjercicioDTO crearRutinaDTO = new CrearRutinaEjercicioDTO();
        crearRutinaDTO.setFechaInicio(LocalDate.of(2023, 10, 27));
        crearRutinaDTO.setDiasEjercicio(Collections.singletonList(lunesDTO));

        RutinaEjercicio savedRutina = new RutinaEjercicio();
        savedRutina.setId(3L);
        savedRutina.setFechaInicio(LocalDate.of(2023, 10, 27));
        DiaEjercicio savedLunes = new DiaEjercicio();
        savedLunes.setId(300L);
        savedLunes.setDiaSemana(DiaSemana.LUNES);
        savedLunes.setEjercicios(Collections.emptyList()); // Empty list
        savedRutina.setDiasEjercicio(Collections.singletonList(savedLunes));
        
        when(rutinaEjercicioRepository.save(any(RutinaEjercicio.class))).thenReturn(savedRutina);

        RutinaEjercicioDTO result = rutinaEjercicioService.crearRutina(crearRutinaDTO);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(LocalDate.of(2023, 10, 27), result.getFechaInicio());
        assertEquals(1, result.getDiasEjercicio().size());
        assertEquals("LUNES", result.getDiasEjercicio().get(0).getDiaSemana());
        assertTrue(result.getDiasEjercicio().get(0).getEjercicios().isEmpty());
        verify(ejerciciosRepository, never()).findById(anyLong()); // No exercises to find
        verify(rutinaEjercicioRepository, times(1)).save(any(RutinaEjercicio.class));
    }

    @Test
    void testCrearRutina_ExerciseNotFound() {
        CrearDiaEjercicioDTO lunesDTO = new CrearDiaEjercicioDTO();
        lunesDTO.setDiaSemana("LUNES");
        lunesDTO.setEjerciciosIds(Arrays.asList(1L, 99L)); // 99L is not found

        CrearRutinaEjercicioDTO crearRutinaDTO = new CrearRutinaEjercicioDTO();
        crearRutinaDTO.setFechaInicio(LocalDate.of(2023, 10, 28));
        crearRutinaDTO.setDiasEjercicio(Collections.singletonList(lunesDTO));

        when(ejerciciosRepository.findById(1L)).thenReturn(Optional.of(ejercicio1));
        when(ejerciciosRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            rutinaEjercicioService.crearRutina(crearRutinaDTO);
        });

        assertEquals("Ejercicio no encontrado con id 99", exception.getMessage());
        verify(ejerciciosRepository, times(2)).findById(anyLong()); // Tried to find 1L and 99L
        verify(rutinaEjercicioRepository, never()).save(any(RutinaEjercicio.class));
    }

    @Test
    void testEliminarRutina_Success() {
        when(rutinaEjercicioRepository.findById(1L)).thenReturn(Optional.of(rutinaEjercicio));
        doNothing().when(rutinaEjercicioRepository).delete(rutinaEjercicio);

        rutinaEjercicioService.eliminarRutina(1L);

        verify(rutinaEjercicioRepository, times(1)).findById(1L);
        verify(rutinaEjercicioRepository, times(1)).delete(rutinaEjercicio);
    }

    @Test
    void testEliminarRutina_NotFound() {
        when(rutinaEjercicioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            rutinaEjercicioService.eliminarRutina(99L);
        });

        assertEquals("Rutina de ejercicio no encontrada con id 99", exception.getMessage());
        verify(rutinaEjercicioRepository, times(1)).findById(99L);
        verify(rutinaEjercicioRepository, never()).delete(any(RutinaEjercicio.class));
    }



}
