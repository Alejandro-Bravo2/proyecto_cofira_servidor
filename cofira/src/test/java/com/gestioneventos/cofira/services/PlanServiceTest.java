package com.gestioneventos.cofira.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.gestioneventos.cofira.dto.plan.CrearPlanDTO;
import com.gestioneventos.cofira.dto.plan.ModificarPlanDTO;
import com.gestioneventos.cofira.dto.plan.PlanDTO;
import com.gestioneventos.cofira.entities.Plan;
import com.gestioneventos.cofira.entities.Usuario;
import com.gestioneventos.cofira.enums.Rol;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.repositories.PlanRepository;
import com.gestioneventos.cofira.repositories.UsuarioRepository;

public class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PlanService planService;

    private Plan plan;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setRol(Rol.USER);

        plan = new Plan();
        plan.setId(1L);
        plan.setPrecio(9.99);
        plan.setSubscripcionActiva(true);
        plan.setUsuario(usuario);
    }

    @Test
    void testListarPlanes_NotEmpty() {
        when(planRepository.findAll()).thenReturn(Collections.singletonList(plan));

        List<PlanDTO> planes = planService.listarPlanes();

        assertNotNull(planes);
        assertEquals(1, planes.size());
        assertEquals(1L, planes.get(0).getId());
        assertEquals(9.99, planes.get(0).getPrecio());
        verify(planRepository, times(1)).findAll();
    }

    @Test
    void testListarPlanes_Empty() {
        when(planRepository.findAll()).thenReturn(Collections.emptyList());

        List<PlanDTO> planes = planService.listarPlanes();

        assertNotNull(planes);
        assertTrue(planes.isEmpty());
        verify(planRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPlan_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        PlanDTO result = planService.obtenerPlan(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(9.99, result.getPrecio());
        verify(planRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPlan_NotFound() {
        when(planRepository.findById(2L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            planService.obtenerPlan(2L);
        });

        assertEquals("Plan no encontrado con id 2", exception.getMessage());
        verify(planRepository, times(1)).findById(2L);
    }

    @Test
    void testObtenerPlanPorUsuario_Success() {
        when(planRepository.findByUsuarioId(1L)).thenReturn(Optional.of(plan));

        PlanDTO result = planService.obtenerPlanPorUsuario(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(9.99, result.getPrecio());
        assertEquals(1L, result.getUsuarioId());
        verify(planRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void testObtenerPlanPorUsuario_NotFound() {
        when(planRepository.findByUsuarioId(2L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            planService.obtenerPlanPorUsuario(2L);
        });

        assertEquals("Plan no encontrado para usuario con id 2", exception.getMessage());
        verify(planRepository, times(1)).findByUsuarioId(2L);
    }

    @Test
    void testCrearPlan_Success() {
        CrearPlanDTO crearDTO = new CrearPlanDTO();
        crearDTO.setPrecio(19.99);
        crearDTO.setSubscripcionActiva(false);
        crearDTO.setUsuarioId(1L);

        Plan newPlan = new Plan();
        newPlan.setId(2L);
        newPlan.setPrecio(crearDTO.getPrecio());
        newPlan.setSubscripcionActiva(crearDTO.getSubscripcionActiva());
        newPlan.setUsuario(usuario);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(planRepository.save(any(Plan.class))).thenReturn(newPlan);

        PlanDTO result = planService.crearPlan(crearDTO);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(19.99, result.getPrecio());
        assertEquals(false, result.getSubscripcionActiva());
        assertEquals(1L, result.getUsuarioId());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testCrearPlan_UsuarioNotFound() {
        CrearPlanDTO crearDTO = new CrearPlanDTO();
        crearDTO.setPrecio(19.99);
        crearDTO.setSubscripcionActiva(false);
        crearDTO.setUsuarioId(99L);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            planService.crearPlan(crearDTO);
        });

        assertEquals("Usuario no encontrado con id 99", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testActualizarPlan_FullUpdateSuccess() {
        ModificarPlanDTO modificarDTO = new ModificarPlanDTO();
        modificarDTO.setPrecio(29.99);
        modificarDTO.setSubscripcionActiva(false);
        modificarDTO.setUsuarioId(1L);

        Plan existingPlan = new Plan();
        existingPlan.setId(1L);
        existingPlan.setPrecio(9.99);
        existingPlan.setSubscripcionActiva(true);
        existingPlan.setUsuario(usuario);

        Plan updatedPlan = new Plan();
        updatedPlan.setId(1L);
        updatedPlan.setPrecio(modificarDTO.getPrecio());
        updatedPlan.setSubscripcionActiva(modificarDTO.getSubscripcionActiva());
        updatedPlan.setUsuario(usuario);

        when(planRepository.findById(1L)).thenReturn(Optional.of(existingPlan));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(planRepository.save(any(Plan.class))).thenReturn(updatedPlan);

        PlanDTO result = planService.actualizarPlan(1L, modificarDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(29.99, result.getPrecio());
        assertEquals(false, result.getSubscripcionActiva());
        assertEquals(1L, result.getUsuarioId());
        verify(planRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testActualizarPlan_PartialUpdatePrice() {
        ModificarPlanDTO modificarDTO = new ModificarPlanDTO();
        modificarDTO.setPrecio(15.00);

        Plan existingPlan = new Plan();
        existingPlan.setId(1L);
        existingPlan.setPrecio(9.99);
        existingPlan.setSubscripcionActiva(true);
        existingPlan.setUsuario(usuario);

        Plan updatedPlan = new Plan();
        updatedPlan.setId(1L);
        updatedPlan.setPrecio(modificarDTO.getPrecio());
        updatedPlan.setSubscripcionActiva(true); // Unchanged
        updatedPlan.setUsuario(usuario); // Unchanged

        when(planRepository.findById(1L)).thenReturn(Optional.of(existingPlan));
        when(planRepository.save(any(Plan.class))).thenReturn(updatedPlan);

        PlanDTO result = planService.actualizarPlan(1L, modificarDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(15.00, result.getPrecio());
        assertEquals(true, result.getSubscripcionActiva()); // Verify unchanged
        verify(planRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).findById(anyLong()); // No user update
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testActualizarPlan_PartialUpdateSubscriptionStatus() {
        ModificarPlanDTO modificarDTO = new ModificarPlanDTO();
        modificarDTO.setSubscripcionActiva(false);

        Plan existingPlan = new Plan();
        existingPlan.setId(1L);
        existingPlan.setPrecio(9.99);
        existingPlan.setSubscripcionActiva(true);
        existingPlan.setUsuario(usuario);

        Plan updatedPlan = new Plan();
        updatedPlan.setId(1L);
        updatedPlan.setPrecio(9.99); // Unchanged
        updatedPlan.setSubscripcionActiva(modificarDTO.getSubscripcionActiva());
        updatedPlan.setUsuario(usuario); // Unchanged

        when(planRepository.findById(1L)).thenReturn(Optional.of(existingPlan));
        when(planRepository.save(any(Plan.class))).thenReturn(updatedPlan);

        PlanDTO result = planService.actualizarPlan(1L, modificarDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(9.99, result.getPrecio()); // Verify unchanged
        assertEquals(false, result.getSubscripcionActiva());
        verify(planRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).findById(anyLong()); // No user update
        verify(planRepository, times(1)).save(any(Plan.class));
    }


    @Test
    void testActualizarPlan_NonExistentPlan() {
        ModificarPlanDTO modificarDTO = new ModificarPlanDTO();
        modificarDTO.setPrecio(99.99);

        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            planService.actualizarPlan(99L, modificarDTO);
        });

        assertEquals("Plan no encontrado con id 99", exception.getMessage());
        verify(planRepository, times(1)).findById(99L);
        verify(usuarioRepository, never()).findById(anyLong());
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testActualizarPlan_NonExistentUserForUpdate() {
        ModificarPlanDTO modificarDTO = new ModificarPlanDTO();
        modificarDTO.setUsuarioId(99L);

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            planService.actualizarPlan(1L, modificarDTO);
        });

        assertEquals("Usuario no encontrado con id 99", exception.getMessage());
        verify(planRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).findById(99L);
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testEliminarPlan_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        doNothing().when(planRepository).delete(plan);

        planService.eliminarPlan(1L);

        verify(planRepository, times(1)).findById(1L);
        verify(planRepository, times(1)).delete(plan);
    }

    @Test
    void testEliminarPlan_NotFound() {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            planService.eliminarPlan(99L);
        });

        assertEquals("Plan no encontrado con id 99", exception.getMessage());
        verify(planRepository, times(1)).findById(99L);
        verify(planRepository, never()).delete(any(Plan.class));
    }




}
