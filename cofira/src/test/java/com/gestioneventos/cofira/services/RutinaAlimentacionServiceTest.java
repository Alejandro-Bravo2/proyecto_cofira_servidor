package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.dto.rutinaalimentacion.*;
import com.gestioneventos.cofira.entities.*;
import com.gestioneventos.cofira.enums.DiaSemana;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.repositories.RutinaAlimentacionRepository;
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

public class RutinaAlimentacionServiceTest {

    @Mock
    private RutinaAlimentacionRepository rutinaAlimentacionRepository;

    @InjectMocks
    private RutinaAlimentacionService rutinaAlimentacionService;

    private RutinaAlimentacion rutinaAlimentacion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup a sample Alimento for meals
        Alimento manzana = new Alimento();
        manzana.setId(1L);
        manzana.setNombre("Manzana");
        manzana.setIngredientes(Collections.singletonList("Manzana"));

        Alimento pollo = new Alimento();
        pollo.setId(2L);
        pollo.setNombre("Pollo");
        pollo.setIngredientes(Collections.singletonList("Pollo"));

        // Setup individual meal entities
        Desayuno desayuno = new Desayuno();
        desayuno.setId(10L);
        desayuno.setAlimentos(Collections.singletonList(manzana));

        Almuerzo almuerzo = new Almuerzo();
        almuerzo.setId(20L);
        almuerzo.setAlimentos(Collections.singletonList(pollo));

        Comida comida = new Comida();
        comida.setId(30L);
        comida.setAlimentos(Collections.singletonList(pollo));

        // Setup a DiaAlimentacion entity
        DiaAlimentacion diaLunes = new DiaAlimentacion();
        diaLunes.setId(100L);
        diaLunes.setDiaSemana(DiaSemana.LUNES);
        diaLunes.setDesayuno(desayuno);
        diaLunes.setAlmuerzo(almuerzo);
        diaLunes.setComida(comida);

        // Setup RutinaAlimentacion entity
        rutinaAlimentacion = new RutinaAlimentacion();
        rutinaAlimentacion.setId(1L);
        rutinaAlimentacion.setFechaInicio(LocalDate.of(2023, 1, 1));
        rutinaAlimentacion.setDiasAlimentacion(Collections.singletonList(diaLunes));
    }

    @Test
    void testListarRutinas_NotEmpty() {
        when(rutinaAlimentacionRepository.findAll()).thenReturn(Collections.singletonList(rutinaAlimentacion));

        List<RutinaAlimentacionDTO> result = rutinaAlimentacionService.listarRutinas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(LocalDate.of(2023, 1, 1), result.get(0).getFechaInicio());
        assertFalse(result.get(0).getDiasAlimentacion().isEmpty());
        assertEquals("LUNES", result.get(0).getDiasAlimentacion().get(0).getDiaSemana());
        assertNotNull(result.get(0).getDiasAlimentacion().get(0).getDesayuno());
        verify(rutinaAlimentacionRepository, times(1)).findAll();
    }

    @Test
    void testListarRutinas_Empty() {
        when(rutinaAlimentacionRepository.findAll()).thenReturn(Collections.emptyList());

        List<RutinaAlimentacionDTO> result = rutinaAlimentacionService.listarRutinas();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rutinaAlimentacionRepository, times(1)).findAll();
    }

    @Test
    void testObtenerRutina_Success() {
        when(rutinaAlimentacionRepository.findById(1L)).thenReturn(Optional.of(rutinaAlimentacion));

        RutinaAlimentacionDTO result = rutinaAlimentacionService.obtenerRutina(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LocalDate.of(2023, 1, 1), result.getFechaInicio());
        assertFalse(result.getDiasAlimentacion().isEmpty());
        assertEquals("LUNES", result.getDiasAlimentacion().get(0).getDiaSemana());
        verify(rutinaAlimentacionRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerRutina_NotFound() {
        when(rutinaAlimentacionRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            rutinaAlimentacionService.obtenerRutina(99L);
        });

        assertEquals("Rutina de alimentación no encontrada con id 99", exception.getMessage());
        verify(rutinaAlimentacionRepository, times(1)).findById(99L);
    }

    @Test
    void testCrearRutina_SuccessWithMeals() {
        // Create DTOs for Alimento
        ComidaDTO manzanaComidaDTO = new ComidaDTO();
        manzanaComidaDTO.setAlimentos(Collections.singletonList("Manzana"));

        ComidaDTO polloComidaDTO = new ComidaDTO();
        polloComidaDTO.setAlimentos(Collections.singletonList("Pollo"));

        // Create DTOs for DiaAlimentacion
        CrearDiaAlimentacionDTO lunesDTO = new CrearDiaAlimentacionDTO();
        lunesDTO.setDiaSemana("LUNES");
        lunesDTO.setDesayuno(manzanaComidaDTO);
        lunesDTO.setAlmuerzo(polloComidaDTO);
        
        CrearDiaAlimentacionDTO martesDTO = new CrearDiaAlimentacionDTO();
        martesDTO.setDiaSemana("MARTES");
        martesDTO.setDesayuno(polloComidaDTO);
        
        CrearRutinaAlimentacionDTO crearRutinaDTO = new CrearRutinaAlimentacionDTO();
        crearRutinaDTO.setFechaInicio(LocalDate.of(2023, 10, 26));
        crearRutinaDTO.setDiasAlimentacion(Arrays.asList(lunesDTO, martesDTO));

        // Mock RutinaAlimentacion entity to be returned by save
        RutinaAlimentacion savedRutina = new RutinaAlimentacion();
        savedRutina.setId(2L);
        savedRutina.setFechaInicio(LocalDate.of(2023, 10, 26));
        
        // Mock a DiaAlimentacion entity for LUNES
        DiaAlimentacion savedLunes = new DiaAlimentacion();
        savedLunes.setId(200L);
        savedLunes.setDiaSemana(DiaSemana.LUNES);
        Desayuno savedDesayuno = new Desayuno();
        savedDesayuno.setAlimentos(Collections.singletonList(new Alimento(null, "Manzana", null)));
        Almuerzo savedAlmuerzo = new Almuerzo();
        savedAlmuerzo.setAlimentos(Collections.singletonList(new Alimento(null, "Pollo", null)));
        savedLunes.setDesayuno(savedDesayuno);
        savedLunes.setAlmuerzo(savedAlmuerzo);

        // Mock a DiaAlimentacion entity for MARTES
        DiaAlimentacion savedMartes = new DiaAlimentacion();
        savedMartes.setId(201L);
        savedMartes.setDiaSemana(DiaSemana.MARTES);
        Desayuno savedMartesDesayuno = new Desayuno();
        savedMartesDesayuno.setAlimentos(Collections.singletonList(new Alimento(null, "Pollo", null)));
        savedMartes.setDesayuno(savedMartesDesayuno);

        savedRutina.setDiasAlimentacion(Arrays.asList(savedLunes, savedMartes));


        when(rutinaAlimentacionRepository.save(any(RutinaAlimentacion.class))).thenReturn(savedRutina);

        RutinaAlimentacionDTO result = rutinaAlimentacionService.crearRutina(crearRutinaDTO);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(LocalDate.of(2023, 10, 26), result.getFechaInicio());
        assertEquals(2, result.getDiasAlimentacion().size());
        assertEquals("LUNES", result.getDiasAlimentacion().get(0).getDiaSemana());
        assertNotNull(result.getDiasAlimentacion().get(0).getDesayuno());
        assertEquals("MARTES", result.getDiasAlimentacion().get(1).getDiaSemana());
        assertNotNull(result.getDiasAlimentacion().get(1).getDesayuno());
        verify(rutinaAlimentacionRepository, times(1)).save(any(RutinaAlimentacion.class));
    }

    @Test
    void testCrearRutina_EmptyMeals() {
        CrearDiaAlimentacionDTO lunesDTO = new CrearDiaAlimentacionDTO();
        lunesDTO.setDiaSemana("LUNES");
        // No meals set

        CrearRutinaAlimentacionDTO crearRutinaDTO = new CrearRutinaAlimentacionDTO();
        crearRutinaDTO.setFechaInicio(LocalDate.of(2023, 10, 27));
        crearRutinaDTO.setDiasAlimentacion(Collections.singletonList(lunesDTO));

        RutinaAlimentacion savedRutina = new RutinaAlimentacion();
        savedRutina.setId(3L);
        savedRutina.setFechaInicio(LocalDate.of(2023, 10, 27));
        DiaAlimentacion savedLunes = new DiaAlimentacion();
        savedLunes.setId(300L);
        savedLunes.setDiaSemana(DiaSemana.LUNES);
        savedRutina.setDiasAlimentacion(Collections.singletonList(savedLunes));

        when(rutinaAlimentacionRepository.save(any(RutinaAlimentacion.class))).thenReturn(savedRutina);

        RutinaAlimentacionDTO result = rutinaAlimentacionService.crearRutina(crearRutinaDTO);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(LocalDate.of(2023, 10, 27), result.getFechaInicio());
        assertEquals(1, result.getDiasAlimentacion().size());
        assertNull(result.getDiasAlimentacion().get(0).getDesayuno()); // Should be null
        verify(rutinaAlimentacionRepository, times(1)).save(any(RutinaAlimentacion.class));
    }

    @Test
    void testEliminarRutina_Success() {
        when(rutinaAlimentacionRepository.findById(1L)).thenReturn(Optional.of(rutinaAlimentacion));
        doNothing().when(rutinaAlimentacionRepository).delete(rutinaAlimentacion);

        rutinaAlimentacionService.eliminarRutina(1L);

        verify(rutinaAlimentacionRepository, times(1)).findById(1L);
        verify(rutinaAlimentacionRepository, times(1)).delete(rutinaAlimentacion);
    }

    @Test
    void testEliminarRutina_NotFound() {
        when(rutinaAlimentacionRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            rutinaAlimentacionService.eliminarRutina(99L);
        });

        assertEquals("Rutina de alimentación no encontrada con id 99", exception.getMessage());
        verify(rutinaAlimentacionRepository, times(1)).findById(99L);
        verify(rutinaAlimentacionRepository, never()).delete(any(RutinaAlimentacion.class));
    }



}
