package com.gestioneventos.cofira.controllers;

import com.gestioneventos.cofira.entities.SalaDeGimnasio;
import com.gestioneventos.cofira.services.SalaDeGimnasioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaDeGimnasioController {

    private final SalaDeGimnasioService salaDeGimnasioService;

    @Autowired
    public SalaDeGimnasioController(SalaDeGimnasioService salaDeGimnasioService) {
        this.salaDeGimnasioService = salaDeGimnasioService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SalaDeGimnasio>> listarSalas() {
        List<SalaDeGimnasio> salas = salaDeGimnasioService.listarSalas();
        return ResponseEntity.ok(salas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SalaDeGimnasio> obtenerSala(@PathVariable Long id) {
        SalaDeGimnasio sala = salaDeGimnasioService.obtenerSala(id);
        return ResponseEntity.ok(sala);
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<SalaDeGimnasio> crearSala(@RequestBody @Valid SalaDeGimnasio sala) {
        SalaDeGimnasio nuevaSala = salaDeGimnasioService.crearSala(sala);
        return ResponseEntity.ok(nuevaSala);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<SalaDeGimnasio> actualizarSala(@PathVariable Long id,
                                                          @RequestBody SalaDeGimnasio sala) {
        SalaDeGimnasio salaActualizada = salaDeGimnasioService.actualizarSala(id, sala);
        return ResponseEntity.ok(salaActualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<?> eliminarSala(@PathVariable Long id) {
        salaDeGimnasioService.eliminarSala(id);
        return ResponseEntity.noContent().build();
    }
}
