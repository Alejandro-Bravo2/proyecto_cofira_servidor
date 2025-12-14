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
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gestioneventos.cofira.dto.objetivos.CrearObjetivosDTO;
import com.gestioneventos.cofira.dto.objetivos.ModificarObjetivosDTO;
import com.gestioneventos.cofira.dto.objetivos.ObjetivosDTO;
import com.gestioneventos.cofira.entities.Objetivos;
import com.gestioneventos.cofira.entities.Usuario;
import com.gestioneventos.cofira.enums.Rol;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.repositories.ObjetivosRepository;
import com.gestioneventos.cofira.repositories.UsuarioRepository;

public class ObjetivosServiceTest {

    @Mock
    private ObjetivosRepository objetivosRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ObjetivosService objetivosService;

    private Objetivos objetivos;
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

        objetivos = new Objetivos();
        objetivos.setId(1L);
        objetivos.setListaObjetivos(Arrays.asList("Perder peso", "Ganar músculo"));
        objetivos.setUsuario(usuario);
    }

    @Test
    void testListarObjetivos_NotEmpty() {
        when(objetivosRepository.findAll()).thenReturn(Collections.singletonList(objetivos));

        List<ObjetivosDTO> objetivosList = objetivosService.listarObjetivos();

        assertNotNull(objetivosList);
        assertEquals(1, objetivosList.size());
        assertEquals(1L, objetivosList.get(0).getId());
        assertEquals("Perder peso", objetivosList.get(0).getListaObjetivos().get(0));
        verify(objetivosRepository, times(1)).findAll();
    }

    @Test
    void testListarObjetivos_Empty() {
        when(objetivosRepository.findAll()).thenReturn(Collections.emptyList());

        List<ObjetivosDTO> objetivosList = objetivosService.listarObjetivos();

        assertNotNull(objetivosList);
        assertTrue(objetivosList.isEmpty());
        verify(objetivosRepository, times(1)).findAll();
    }

    @Test
    void testObtenerObjetivos_Success() {
        when(objetivosRepository.findById(1L)).thenReturn(Optional.of(objetivos));

        ObjetivosDTO result = objetivosService.obtenerObjetivos(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Perder peso", result.getListaObjetivos().get(0));
        verify(objetivosRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerObjetivos_NotFound() {
        when(objetivosRepository.findById(2L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            objetivosService.obtenerObjetivos(2L);
        });

        assertEquals("Objetivos no encontrados con id 2", exception.getMessage());
        verify(objetivosRepository, times(1)).findById(2L);
    }

    @Test
    void testObtenerObjetivosPorUsuario_Success() {
        when(objetivosRepository.findByUsuarioId(1L)).thenReturn(Optional.of(objetivos));

        ObjetivosDTO result = objetivosService.obtenerObjetivosPorUsuario(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Perder peso", result.getListaObjetivos().get(0));
        assertEquals(1L, result.getUsuarioId());
        verify(objetivosRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void testObtenerObjetivosPorUsuario_NotFound() {
        when(objetivosRepository.findByUsuarioId(2L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            objetivosService.obtenerObjetivosPorUsuario(2L);
        });

        assertEquals("Objetivos no encontrados para usuario con id 2", exception.getMessage());
        verify(objetivosRepository, times(1)).findByUsuarioId(2L);
    }

    @Test
    void testCrearObjetivos_Success() {
        CrearObjetivosDTO crearDTO = new CrearObjetivosDTO();
        crearDTO.setListaObjetivos(Arrays.asList("Mantener peso", "Correr 5k"));
        crearDTO.setUsuarioId(1L);

        Objetivos newObjetivos = new Objetivos();
        newObjetivos.setId(2L);
        newObjetivos.setListaObjetivos(crearDTO.getListaObjetivos());
        newObjetivos.setUsuario(usuario); // Use the pre-defined user mock

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(objetivosRepository.save(any(Objetivos.class))).thenReturn(newObjetivos);

        ObjetivosDTO result = objetivosService.crearObjetivos(crearDTO);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(1L, result.getUsuarioId());
        assertEquals("Mantener peso", result.getListaObjetivos().get(0));
        verify(usuarioRepository, times(1)).findById(1L);
        verify(objetivosRepository, times(1)).save(any(Objetivos.class));
    }

    @Test
    void testCrearObjetivos_UsuarioNotFound() {
        CrearObjetivosDTO crearDTO = new CrearObjetivosDTO();
        crearDTO.setListaObjetivos(Arrays.asList("Objetivo X"));
        crearDTO.setUsuarioId(99L);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            objetivosService.crearObjetivos(crearDTO);
        });

        assertEquals("Usuario no encontrado con id 99", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
        verify(objetivosRepository, never()).save(any(Objetivos.class));
    }

    @Test
    void testActualizarObjetivos_Success() {
        ModificarObjetivosDTO modificarDTO = new ModificarObjetivosDTO();
        modificarDTO.setListaObjetivos(Arrays.asList("Correr maratón", "Nadar 1km"));

        Objetivos updatedObjetivos = new Objetivos();
        updatedObjetivos.setId(1L);
        updatedObjetivos.setListaObjetivos(modificarDTO.getListaObjetivos());
        updatedObjetivos.setUsuario(usuario);

        when(objetivosRepository.findById(1L)).thenReturn(Optional.of(objetivos));
        when(objetivosRepository.save(any(Objetivos.class))).thenReturn(updatedObjetivos);

        ObjetivosDTO result = objetivosService.actualizarObjetivos(1L, modificarDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Correr maratón", result.getListaObjetivos().get(0));
        assertEquals("Nadar 1km", result.getListaObjetivos().get(1));
        verify(objetivosRepository, times(1)).findById(1L);
        verify(objetivosRepository, times(1)).save(any(Objetivos.class));
    }

    @Test
    void testActualizarObjetivos_PartialUpdate() {
        ModificarObjetivosDTO modificarDTO = new ModificarObjetivosDTO();
        modificarDTO.setListaObjetivos(Collections.singletonList("Nuevo objetivo único")); // Only update listaObjetivos

        Objetivos existingObjetivos = new Objetivos();
        existingObjetivos.setId(1L);
        existingObjetivos.setListaObjetivos(Arrays.asList("Old objective 1", "Old objective 2"));
        existingObjetivos.setUsuario(usuario);

        Objetivos updatedObjetivos = new Objetivos();
        updatedObjetivos.setId(1L);
        updatedObjetivos.setListaObjetivos(modificarDTO.getListaObjetivos());
        updatedObjetivos.setUsuario(usuario);

        when(objetivosRepository.findById(1L)).thenReturn(Optional.of(existingObjetivos));
        when(objetivosRepository.save(any(Objetivos.class))).thenReturn(updatedObjetivos);

        ObjetivosDTO result = objetivosService.actualizarObjetivos(1L, modificarDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getListaObjetivos().size());
        assertEquals("Nuevo objetivo único", result.getListaObjetivos().get(0));
        verify(objetivosRepository, times(1)).findById(1L);
        verify(objetivosRepository, times(1)).save(any(Objetivos.class));
    }

    @Test
    void testActualizarObjetivos_NotFound() {
        ModificarObjetivosDTO modificarDTO = new ModificarObjetivosDTO();
        modificarDTO.setListaObjetivos(Arrays.asList("No Existe"));

        when(objetivosRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            objetivosService.actualizarObjetivos(99L, modificarDTO);
        });

        assertEquals("Objetivos no encontrados con id 99", exception.getMessage());
        verify(objetivosRepository, times(1)).findById(99L);
        verify(objetivosRepository, never()).save(any(Objetivos.class));
    }

    @Test
    void testEliminarObjetivos_Success() {
        when(objetivosRepository.findById(1L)).thenReturn(Optional.of(objetivos));
        doNothing().when(objetivosRepository).delete(objetivos);

        objetivosService.eliminarObjetivos(1L);

        verify(objetivosRepository, times(1)).findById(1L);
        verify(objetivosRepository, times(1)).delete(objetivos);
    }

    @Test
    void testEliminarObjetivos_NotFound() {
        when(objetivosRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            objetivosService.eliminarObjetivos(99L);
        });

        assertEquals("Objetivos no encontrados con id 99", exception.getMessage());
        verify(objetivosRepository, times(1)).findById(99L);
        verify(objetivosRepository, never()).delete(any(Objetivos.class));
    }




}
