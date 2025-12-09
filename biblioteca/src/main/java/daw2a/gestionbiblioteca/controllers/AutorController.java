package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.autor.AutorDTO;
import daw2a.gestionbiblioteca.dto.autor.CrearAutorDTO;
import daw2a.gestionbiblioteca.dto.autor.ModificarAutorDTO;
import daw2a.gestionbiblioteca.services.AutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autores")
@CrossOrigin(origins = "http://example.com")
public class AutorController {
    private final AutorService autorService;

    @Autowired
    public AutorController(AutorService autorRepository) {
        this.autorService = autorRepository;
    }

    //Listar todos los autores
    @GetMapping
    @CrossOrigin(origins = "http://example.com")
    public ResponseEntity<Page<?>> listarAutores(@RequestParam(required = false) String nombre,
                                                 @RequestParam(defaultValue = "false") boolean conLibros,
                                                 Pageable pageable) {
        Page<?> autores = autorService.listarAutores(nombre, conLibros, pageable);
        return ResponseEntity.ok(autores);
    }

    //Obtener los detalles de un autor específico
    @GetMapping("/{id}")
    public ResponseEntity<AutorDTO> obtenerAutor(@PathVariable Long id) {
        AutorDTO autor = autorService.obtenerAutor(id);
        return ResponseEntity.ok(autor);
    }

    // Crear un nuevo autor
    @PostMapping
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<AutorDTO> crearAutor(@RequestBody @Valid CrearAutorDTO crearAutorDTO) {
        AutorDTO nuevoAutor = autorService.crearAutor(crearAutorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAutor);
        /*return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/autores/" + nuevoAutor.getId())
                .body(nuevoAutor);*/
        /*return ResponseEntity.created(URI.create("/autores/" + nuevoAutor.getId()))
                .body(nuevoAutor);*/
        /*return ResponseEntity.ok(autorService.crearAutor(crearAutorDTO));*/
    }

    // Actualizar un autor existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<AutorDTO> actualizarAutor(@PathVariable Long id,
                                                    @RequestBody @Valid ModificarAutorDTO modificarAutorDTO) {
        return ResponseEntity.ok(autorService.actualizarAutor(id, modificarAutorDTO));
    }

    // Eliminar un autor
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Void> eliminarAutor(@PathVariable Long id) {
        autorService.eliminarAutor(id);
        return ResponseEntity.noContent().build();
    }
}
