package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.libro.CrearLibroDTO;
import daw2a.gestionbiblioteca.dto.libro.LibroDetalleDTO;
import daw2a.gestionbiblioteca.dto.libro.LibroListadoDTO;
import daw2a.gestionbiblioteca.dto.libro.ModificarLibroDTO;
import daw2a.gestionbiblioteca.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/libros")
public class LibroController {

    private final LibroService libroService;

    @Autowired
    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    // Listar todos los libros con paginación y filtros opcionales
    @GetMapping
    public ResponseEntity<Page<LibroListadoDTO>> listarLibros(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String genero,
            Pageable pageable) {
        Page<LibroListadoDTO> libros = libroService.listarLibros(titulo, genero, pageable);
        return ResponseEntity.ok(libros);
    }

    // Obtener los detalles de un libro específico
    @GetMapping("/{id}")
    public ResponseEntity<LibroDetalleDTO> obtenerLibro(@PathVariable Long id) {
        LibroDetalleDTO libro = libroService.obtenerLibro(id);
        return ResponseEntity.ok(libro);
    }

    // Crear un nuevo libro (solo bibliotecario)
    @PostMapping
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<LibroDetalleDTO> crearLibro(@RequestBody @Valid CrearLibroDTO crearLibroDTO) {
        LibroDetalleDTO nuevoLibro = libroService.crearLibro(crearLibroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    // Actualizar un libro existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<LibroDetalleDTO> actualizarLibro(
            @PathVariable Long id,
            @RequestBody @Valid ModificarLibroDTO modificarLibroDTO) {
        LibroDetalleDTO libroActualizado = libroService.actualizarLibro(id, modificarLibroDTO);
        return ResponseEntity.ok(libroActualizado);
    }

    // Eliminar un libro
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.borrarLibro(id);
        return ResponseEntity.noContent().build();
    }
}