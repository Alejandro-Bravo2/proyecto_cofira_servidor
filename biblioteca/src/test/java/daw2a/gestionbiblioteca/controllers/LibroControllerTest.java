package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.libro.CrearLibroDTO;
import daw2a.gestionbiblioteca.dto.libro.LibroDetalleDTO;
import daw2a.gestionbiblioteca.dto.libro.LibroListadoDTO;
import daw2a.gestionbiblioteca.dto.libro.ModificarLibroDTO;
import daw2a.gestionbiblioteca.services.LibroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibroControllerTest {

    @Mock
    private LibroService libroService;

    @InjectMocks
    private LibroController libroController;

    private LibroDetalleDTO libroDetalleDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        libroDetalleDTO = new LibroDetalleDTO();
        libroDetalleDTO.setId(1L);
        libroDetalleDTO.setTitulo("Cien años de soledad");
        libroDetalleDTO.setGenero("Novela");
        libroDetalleDTO.setAnyoPublicacion("1967");
        libroDetalleDTO.setEstado("disponible");
        libroDetalleDTO.setNombreAutor("Gabriel García Márquez");
    }

    @Test
    void listarLibros() {
        PageRequest pageable = PageRequest.of(0, 10);
        LibroListadoDTO libroListadoDTO = new LibroListadoDTO();
        libroListadoDTO.setId(1L);
        libroListadoDTO.setTitulo("Cien años de soledad");
        libroListadoDTO.setGenero("Novela");

        Page<LibroListadoDTO> page = new PageImpl<>(List.of(libroListadoDTO));
        when(libroService.listarLibros(null, null, pageable)).thenReturn(page);

        ResponseEntity<Page<LibroListadoDTO>> response = libroController.listarLibros(null, null, pageable);
        Page<LibroListadoDTO> result = response.getBody();

        assertEquals(1, result.getTotalElements());
        assertEquals("Cien años de soledad", result.getContent().get(0).getTitulo());
        verify(libroService, times(1)).listarLibros(null, null, pageable);
    }

    @Test
    void obtenerLibro() {
        when(libroService.obtenerLibro(1L)).thenReturn(libroDetalleDTO);

        ResponseEntity<LibroDetalleDTO> response = libroController.obtenerLibro(1L);

        assertEquals(ResponseEntity.ok(libroDetalleDTO), response);
        assertEquals(libroDetalleDTO, response.getBody());
        verify(libroService, times(1)).obtenerLibro(1L);
    }

    @Test
    void crearLibro() {
        CrearLibroDTO crearLibroDTO = new CrearLibroDTO();
        crearLibroDTO.setTitulo("Cien años de soledad");
        crearLibroDTO.setGenero("Novela");
        crearLibroDTO.setAnyoPublicacion("1967");
        crearLibroDTO.setAutorId(1L);

        when(libroService.crearLibro(crearLibroDTO)).thenReturn(libroDetalleDTO);

        ResponseEntity<LibroDetalleDTO> response = libroController.crearLibro(crearLibroDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(libroDetalleDTO, response.getBody());
        verify(libroService, times(1)).crearLibro(crearLibroDTO);
    }

    @Test
    void actualizarLibro() {
        ModificarLibroDTO modificarLibroDTO = new ModificarLibroDTO();
        modificarLibroDTO.setTitulo("Cien años de soledad");
        modificarLibroDTO.setGenero("Novela");
        modificarLibroDTO.setAnyoPublicacion("1967");

        when(libroService.actualizarLibro(1L, modificarLibroDTO)).thenReturn(libroDetalleDTO);

        ResponseEntity<LibroDetalleDTO> response = libroController.actualizarLibro(1L, modificarLibroDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(libroDetalleDTO, response.getBody());
        verify(libroService, times(1)).actualizarLibro(1L, modificarLibroDTO);
    }

    @Test
    void eliminarLibro() {
        doNothing().when(libroService).borrarLibro(1L);

        ResponseEntity<Void> response = libroController.eliminarLibro(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(libroService, times(1)).borrarLibro(1L);
    }
}