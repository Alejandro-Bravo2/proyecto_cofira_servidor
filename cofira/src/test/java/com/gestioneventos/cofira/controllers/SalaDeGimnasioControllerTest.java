package com.gestioneventos.cofira.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestioneventos.cofira.dto.sala.CrearSalaDTO;
import com.gestioneventos.cofira.dto.sala.SalaDTO;
import com.gestioneventos.cofira.services.SalaDeGimnasioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests del SalaDeGimnasioController")
class SalaDeGimnasioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalaDeGimnasioService salaDeGimnasioService;

    private SalaDTO salaDTO;
    private CrearSalaDTO crearSalaDTO;

    @BeforeEach
    void setUp() {
        salaDTO = new SalaDTO();
        salaDTO.setId(1L);
        salaDTO.setFechaInicio(LocalDate.of(2025, 1, 1));
        salaDTO.setFechaFin(LocalDate.of(2025, 12, 31));

        crearSalaDTO = new CrearSalaDTO();
        crearSalaDTO.setFechaInicio(LocalDate.of(2025, 1, 1));
        crearSalaDTO.setFechaFin(LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("GET /api/salas - Usuario autenticado puede listar salas")
    @WithMockUser(username = "user", roles = {"USER"})
    void listarSalas_Autenticado_RetornaOk() throws Exception {
        when(salaDeGimnasioService.listarSalas()).thenReturn(Arrays.asList(salaDTO));

        mockMvc.perform(get("/api/salas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(salaDeGimnasioService, times(1)).listarSalas();
    }

    @Test
    @DisplayName("POST /api/salas - ADMIN puede crear sala")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crearSala_ComoAdmin_RetornaCreated() throws Exception {
        when(salaDeGimnasioService.crearSala(any())).thenReturn(salaDTO);

        mockMvc.perform(post("/api/salas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearSalaDTO)))
                .andExpect(status().isCreated());

        verify(salaDeGimnasioService, times(1)).crearSala(any());
    }

    @Test
    @DisplayName("POST /api/salas - USER no puede crear sala")
    @WithMockUser(username = "user", roles = {"USER"})
    void crearSala_ComoUser_Retorna403() throws Exception {
        mockMvc.perform(post("/api/salas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearSalaDTO)))
                .andExpect(status().isForbidden());

        verify(salaDeGimnasioService, never()).crearSala(any());
    }
}
