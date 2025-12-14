package com.gestioneventos.cofira.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestioneventos.cofira.dto.objetivos.CrearObjetivosDTO;
import com.gestioneventos.cofira.dto.objetivos.ModificarObjetivosDTO;
import com.gestioneventos.cofira.dto.objetivos.ObjetivosDTO;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.services.ObjetivosService;
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
@DisplayName("Tests del ObjetivosController")
class ObjetivosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ObjetivosService objetivosService;

    private ObjetivosDTO objetivosDTO;
    private CrearObjetivosDTO crearObjetivosDTO;

    @BeforeEach
    void setUp() {
        objetivosDTO = new ObjetivosDTO();
        objetivosDTO.setId(1L);
        objetivosDTO.setListaObjetivos(Arrays.asList("Perder peso"));
        objetivosDTO.setUsuarioId(1L);

        crearObjetivosDTO = new CrearObjetivosDTO();
        crearObjetivosDTO.setListaObjetivos(Arrays.asList("Perder peso"));
        crearObjetivosDTO.setUsuarioId(1L);
    }

    @Test
    @DisplayName("GET /api/objetivos - Listar objetivos")
    @WithMockUser(username = "user", roles = {"USER"})
    void listarObjetivos_RetornaOk() throws Exception {
        when(objetivosService.listarObjetivos()).thenReturn(Arrays.asList(objetivosDTO));

        mockMvc.perform(get("/api/objetivos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(objetivosService, times(1)).listarObjetivos();
    }

    @Test
    @DisplayName("POST /api/objetivos - Crear objetivos")
    @WithMockUser(username = "user", roles = {"USER"})
    void crearObjetivos_RetornaCreated() throws Exception {
        when(objetivosService.crearObjetivos(any())).thenReturn(objetivosDTO);

        mockMvc.perform(post("/api/objetivos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearObjetivosDTO)))
                .andExpect(status().isCreated());

        verify(objetivosService, times(1)).crearObjetivos(any());
    }
}
