package com.gestioneventos.cofira.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestioneventos.cofira.dto.rutinaejercicio.CrearRutinaEjercicioDTO;
import com.gestioneventos.cofira.dto.rutinaejercicio.RutinaEjercicioDTO;
import com.gestioneventos.cofira.services.RutinaEjercicioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests del RutinaEjercicioController")
class RutinaEjercicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RutinaEjercicioService rutinaEjercicioService;

    private RutinaEjercicioDTO rutinaDTO;
    private CrearRutinaEjercicioDTO crearRutinaDTO;

    @BeforeEach
    void setUp() {
        rutinaDTO = new RutinaEjercicioDTO();
        rutinaDTO.setId(1L);
        rutinaDTO.setFechaInicio(LocalDate.of(2025, 1, 1));
        rutinaDTO.setDiasEjercicio(Collections.emptyList());

        crearRutinaDTO = new CrearRutinaEjercicioDTO();
        crearRutinaDTO.setFechaInicio(LocalDate.of(2025, 1, 1));
        crearRutinaDTO.setDiasEjercicio(Collections.emptyList());
    }

    @Test
    @DisplayName("GET /api/rutinas-ejercicio - Listar rutinas")
    @WithMockUser(username = "user", roles = {"USER"})
    void listarRutinas_RetornaOk() throws Exception {
        when(rutinaEjercicioService.listarRutinas()).thenReturn(Arrays.asList(rutinaDTO));

        mockMvc.perform(get("/api/rutinas-ejercicio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(rutinaEjercicioService, times(1)).listarRutinas();
    }

    @Test
    @DisplayName("POST /api/rutinas-ejercicio - Crear rutina")
    @WithMockUser(username = "user", roles = {"USER"})
    void crearRutina_RetornaCreated() throws Exception {
        when(rutinaEjercicioService.crearRutina(any())).thenReturn(rutinaDTO);

        mockMvc.perform(post("/api/rutinas-ejercicio")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRutinaDTO)))
                .andExpect(status().isCreated());

        verify(rutinaEjercicioService, times(1)).crearRutina(any());
    }
}
