package com.gestioneventos.cofira.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestioneventos.cofira.dto.plan.CrearPlanDTO;
import com.gestioneventos.cofira.dto.plan.ModificarPlanDTO;
import com.gestioneventos.cofira.dto.plan.PlanDTO;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.services.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests del PlanController")
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanService planService;

    private PlanDTO planDTO;
    private CrearPlanDTO crearPlanDTO;
    private ModificarPlanDTO modificarPlanDTO;

    @BeforeEach
    void setUp() {
        planDTO = new PlanDTO();
        planDTO.setId(1L);
        planDTO.setPrecio(99.99);
        planDTO.setSubscripcionActiva(true);
        planDTO.setUsuarioId(1L);

        crearPlanDTO = new CrearPlanDTO();
        crearPlanDTO.setPrecio(99.99);
        crearPlanDTO.setSubscripcionActiva(true);
        crearPlanDTO.setUsuarioId(1L);

        modificarPlanDTO = new ModificarPlanDTO();
        modificarPlanDTO.setPrecio(149.99);
        modificarPlanDTO.setSubscripcionActiva(false);
    }

    @Test
    @DisplayName("GET /api/planes - Usuario autenticado puede listar planes")
    @WithMockUser(username = "user", roles = {"USER"})
    void listarPlanes_Autenticado_RetornaOk() throws Exception {
        List<PlanDTO> planes = Arrays.asList(planDTO);
        when(planService.listarPlanes()).thenReturn(planes);

        mockMvc.perform(get("/api/planes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].precio").value(99.99));

        verify(planService, times(1)).listarPlanes();
    }

    @Test
    @DisplayName("GET /api/planes/{id} - Obtener plan existente")
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerPlan_Existente_RetornaOk() throws Exception {
        when(planService.obtenerPlan(1L)).thenReturn(planDTO);

        mockMvc.perform(get("/api/planes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precio").value(99.99));

        verify(planService, times(1)).obtenerPlan(1L);
    }

    @Test
    @DisplayName("GET /api/planes/{id} - Obtener plan inexistente retorna 404")
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerPlan_Inexistente_Retorna404() throws Exception {
        when(planService.obtenerPlan(99L)).thenThrow(new RecursoNoEncontradoException("Plan no encontrado"));

        mockMvc.perform(get("/api/planes/99"))
                .andExpect(status().isNotFound());

        verify(planService, times(1)).obtenerPlan(99L);
    }

    @Test
    @DisplayName("GET /api/planes/usuario/{usuarioId} - Obtener plan por usuario")
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerPlanPorUsuario_Existente_RetornaOk() throws Exception {
        when(planService.obtenerPlanPorUsuario(1L)).thenReturn(planDTO);

        mockMvc.perform(get("/api/planes/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.usuarioId").value(1));

        verify(planService, times(1)).obtenerPlanPorUsuario(1L);
    }

    @Test
    @DisplayName("POST /api/planes - Crear plan con datos válidos")
    @WithMockUser(username = "user", roles = {"USER"})
    void crearPlan_DatosValidos_Retorna201() throws Exception {
        when(planService.crearPlan(any(CrearPlanDTO.class))).thenReturn(planDTO);

        mockMvc.perform(post("/api/planes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearPlanDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precio").value(99.99));

        verify(planService, times(1)).crearPlan(any(CrearPlanDTO.class));
    }

    @Test
    @DisplayName("POST /api/planes - Crear plan con datos inválidos retorna 400")
    @WithMockUser(username = "user", roles = {"USER"})
    void crearPlan_DatosInvalidos_Retorna400() throws Exception {
        CrearPlanDTO dtoInvalido = new CrearPlanDTO();
        // No setear campos requeridos

        mockMvc.perform(post("/api/planes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(planService, never()).crearPlan(any(CrearPlanDTO.class));
    }

    @Test
    @DisplayName("PUT /api/planes/{id} - Actualizar plan existente")
    @WithMockUser(username = "user", roles = {"USER"})
    void actualizarPlan_Existente_RetornaOk() throws Exception {
        PlanDTO planActualizado = new PlanDTO();
        planActualizado.setId(1L);
        planActualizado.setPrecio(149.99);
        planActualizado.setSubscripcionActiva(false);

        when(planService.actualizarPlan(eq(1L), any(ModificarPlanDTO.class))).thenReturn(planActualizado);

        mockMvc.perform(put("/api/planes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modificarPlanDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.precio").value(149.99))
                .andExpect(jsonPath("$.subscripcionActiva").value(false));

        verify(planService, times(1)).actualizarPlan(eq(1L), any(ModificarPlanDTO.class));
    }

    @Test
    @DisplayName("PUT /api/planes/{id} - Actualizar plan inexistente retorna 404")
    @WithMockUser(username = "user", roles = {"USER"})
    void actualizarPlan_Inexistente_Retorna404() throws Exception {
        when(planService.actualizarPlan(eq(99L), any(ModificarPlanDTO.class)))
                .thenThrow(new RecursoNoEncontradoException("Plan no encontrado"));

        mockMvc.perform(put("/api/planes/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modificarPlanDTO)))
                .andExpect(status().isNotFound());

        verify(planService, times(1)).actualizarPlan(eq(99L), any(ModificarPlanDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/planes/{id} - Eliminar plan existente")
    @WithMockUser(username = "user", roles = {"USER"})
    void eliminarPlan_Existente_Retorna204() throws Exception {
        doNothing().when(planService).eliminarPlan(1L);

        mockMvc.perform(delete("/api/planes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(planService, times(1)).eliminarPlan(1L);
    }

    @Test
    @DisplayName("DELETE /api/planes/{id} - Eliminar plan inexistente retorna 404")
    @WithMockUser(username = "user", roles = {"USER"})
    void eliminarPlan_Inexistente_Retorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("Plan no encontrado")).when(planService).eliminarPlan(99L);

        mockMvc.perform(delete("/api/planes/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(planService, times(1)).eliminarPlan(99L);
    }
}
