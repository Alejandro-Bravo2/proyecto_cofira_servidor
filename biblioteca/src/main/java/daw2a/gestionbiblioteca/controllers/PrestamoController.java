package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.prestamo.CrearPrestamoDTO;
import daw2a.gestionbiblioteca.dto.prestamo.PrestamoDTO;
import daw2a.gestionbiblioteca.services.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @Autowired
    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // Listar todos los préstamos con paginación
    @GetMapping
    public ResponseEntity<Page<PrestamoDTO>> listarPrestamos(Pageable pageable) {
        Page<PrestamoDTO> prestamos = prestamoService.listarPrestamos(pageable);
        return ResponseEntity.ok(prestamos);
    }

    // Obtener los detalles de un préstamo específico
    @GetMapping("/{id}")
    public ResponseEntity<PrestamoDTO> obtenerPrestamo(@PathVariable Long id) {
        PrestamoDTO prestamoDTO = prestamoService.obtenerPrestamoPorId(id);
        return ResponseEntity.ok(prestamoDTO);
    }

    // Crear un nuevo préstamo
    @PostMapping
    public ResponseEntity<PrestamoDTO> crearPrestamo(@RequestBody @Valid CrearPrestamoDTO prestamoCrearDTO) {
        PrestamoDTO nuevoPrestamo = prestamoService.crearPrestamo(prestamoCrearDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPrestamo);
    }

    // Actualizar un préstamo existente
    @PutMapping("/{id}")
    public ResponseEntity<PrestamoDTO> actualizarPrestamo(@PathVariable Long id, @RequestBody @Valid CrearPrestamoDTO prestamoActualizadoDTO) {
        PrestamoDTO prestamo = prestamoService.actualizarPrestamo(id, prestamoActualizadoDTO);
        return ResponseEntity.ok(prestamo);
    }

    // Endpoint para renovar un préstamo
    @PutMapping("/renovar/{id}")
    public ResponseEntity<PrestamoDTO> renovarPrestamo(@PathVariable Long id, @RequestParam int diasExtension) {
        PrestamoDTO prestamoRenovado = prestamoService.renovarPrestamo(id, diasExtension);
        return ResponseEntity.ok(prestamoRenovado);
    }

    // Registrar la devolución de un préstamo
    @PutMapping("/devolver/{id}")
    public ResponseEntity<PrestamoDTO> devolverPrestamo(@PathVariable Long id) {
        PrestamoDTO prestamo = prestamoService.devolverPrestamo(id);
        return ResponseEntity.ok(prestamo);
    }

    // Eliminar un préstamo
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        prestamoService.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}