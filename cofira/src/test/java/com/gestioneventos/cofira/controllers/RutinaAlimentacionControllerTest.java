package com.gestioneventos.cofira.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestioneventos.cofira.dto.rutinaalimentacion.CrearRutinaAlimentacionDTO;
import com.gestioneventos.cofira.dto.rutinaalimentacion.RutinaAlimentacionDTO;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.services.RutinaAlimentacionService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests del RutinaAlimentacionController")
class RutinaAlimentacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RutinaAlimentacionService rutinaAlimentacionService;

    private RutinaAlimentacionDTO rutinaDTO;
    private CrearRutinaAlimentacionDTO crearRutinaDTO;

    @BeforeEach
    void setUp() {
        rutinaDTO = new RutinaAlimentacionDTO();
        rutinaDTO.setId(1L);
        rutinaDTO.setFechaInicio(LocalDate.of(2025, 1, 1));
        rutinaDTO.setDiasAlimentacion(Collections.emptyList());

        crearRutinaDTO = new CrearRutinaAlimentacionDTO();
        crearRutinaDTO.setFechaInicio(LocalDate.of(2025, 1, 1));
        crearRutinaDTO.setDiasAlimentacion(Collections.emptyList());
    }

    @Test
    @DisplayName("GET /api/rutinas-alimentacion - Usuario autenticado puede listar rutinas")
    @WithMockUser(username = "user", roles = {"USER"})
    void listarRutinas_Autenticado_RetornaOk() throws Exception {
        List<RutinaAlimentacionDTO> rutinas = Arrays.asList(rutinaDTO);
        when(rutinaAlimentacionService.listarRutinas()).thenReturn(rutinas);

        mockMvc.perform(get("/api/rutinas-alimentacion"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(rutinaAlimentacionService, times(1)).listarRutinas();
    }

    @Test
    @DisplayName("GET /api/rutinas-alimentacion/{id} - Obtener rutina existente")
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerRutina_Existente_RetornaOk() throws Exception {
        when(rutinaAlimentacionService.obtenerRutina(1L)).thenReturn(rutinaDTO);

        mockMvc.perform(get("/api/rutinas-alimentacion/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(rutinaAlimentacionService, times(1)).obtenerRutina(1L);
    }

    @Test
    @DisplayName("GET /api/rutinas-alimentacion/{id} - Obtener rutina inexistente retorna 404")
    @WithMockUser(username = "user", roles = {"USER"})
    void obtenerRutina_Inexistente_Retorna404() throws Exception {
        when(rutinaAlimentacionService.obtenerRutina(99L))
                .thenThrow(new RecursoNoEncontradoException("Rutina no encontrada"));

        mockMvc.perform(get("/api/rutinas-alimentacion/99"))
                .andExpect(status().isNotFound());

        verify(rutinaAlimentacionService, times(1)).obtenerRutina(99L);
    }

    @Test
    @DisplayName("POST /api/rutinas-alimentacion - Crear rutina con datos válidos")
    @WithMockUser(username = "user", roles = {"USER"})
    void crearRutina_DatosValidos_Retorna201() throws Exception {
        when(rutinaAlimentacionService.crearRutina(any(CrearRutinaAlimentacionDTO.class)))
                .thenReturn(rutinaDTO);

        mockMvc.perform(post("/api/rutinas-alimentacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRutinaDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(rutinaAlimentacionService, times(1))
                .crearRutina(any(CrearRutinaAlimentacionDTO.class));
    }

    @Test
    @DisplayName("POST /api/rutinas-alimentacion - Crear rutina con datos inválidos retorna 400")
    @WithMockUser(username = "user", roles = {"USER"})
    void crearRutina_DatosInvalidos_Retorna400() throws Exception {
        CrearRutinaAlimentacionDTO dtoInvalido = new CrearRutinaAlimentacionDTO();
        // No setear campos requeridos

        mockMvc.perform(post("/api/rutinas-alimentacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(rutinaAlimentacionService, never())
                .crearRutina(any(CrearRutinaAlimentacionDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/rutinas-alimentacion/{id} - Eliminar rutina existente")
    @WithMockUser(username = "user", roles = {"USER"})
    void eliminarRutina_Existente_Retorna204() throws Exception {
        doNothing().when(rutinaAlimentacionService).eliminarRutina(1L);

        mockMvc.perform(delete("/api/rutinas-alimentacion/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(rutinaAlimentacionService, times(1)).eliminarRutina(1L);
    }

    @Test
    @DisplayName("DELETE /api/rutinas-alimentacion/{id} - Eliminar rutina inexistente retorna 404")
    @WithMockUser(username = "user", roles = {"USER"})
    void eliminarRutina_Inexistente_Retorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("Rutina no encontrada"))
                .when(rutinaAlimentacionService).eliminarRutina(99L);

        mockMvc.perform(delete("/api/rutinas-alimentacion/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(rutinaAlimentacionService, times(1)).eliminarRutina(99L);
    }

    @Test
    @DisplayName("GET /api/rutinas-alimentacion - Listar rutinas vacías")
    @WithMockUser(username = "user", roles = {"USER"})
    void listarRutinas_ListaVacia_RetornaOk() throws Exception {
        when(rutinaAlimentacionService.listarRutinas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rutinas-alimentacion"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(rutinaAlimentacionService, times(1)).listarRutinas();
    }

    @Test
    @DisplayName("POST /api/rutinas-alimentacion - ADMIN puede crear rutina")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crearRutina_ComoAdmin_Retorna201() throws Exception {
        when(rutinaAlimentacionService.crearRutina(any(CrearRutinaAlimentacionDTO.class)))
                .thenReturn(rutinaDTO);

        mockMvc.perform(post("/api/rutinas-alimentacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRutinaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(rutinaAlimentacionService, times(1))
                .crearRutina(any(CrearRutinaAlimentacionDTO.class));
    }
}
