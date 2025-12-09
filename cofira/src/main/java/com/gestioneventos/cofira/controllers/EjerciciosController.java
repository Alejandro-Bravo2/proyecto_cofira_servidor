package com.gestioneventos.cofira.controllers;

import com.gestioneventos.cofira.entities.Ejercicios;
import com.gestioneventos.cofira.services.EjerciciosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ejercicios")
public class EjerciciosController {

    private final EjerciciosService ejerciciosService;

    @Autowired
    public EjerciciosController(EjerciciosService ejerciciosService) {
        this.ejerciciosService = ejerciciosService;
    }

    @GetMapping
    public ResponseEntity<List<Ejercicios>> listarEjercicios() {
        List<Ejercicios> ejercicios = ejerciciosService.listarEjercicios();
        return ResponseEntity.ok(ejercicios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ejercicios> obtenerEjercicio(@PathVariable Long id) {
        Ejercicios ejercicio = ejerciciosService.obtenerEjercicio(id);
        return ResponseEntity.ok(ejercicio);
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<Ejercicios>> obtenerEjerciciosPorSala(@PathVariable Long salaId) {
        List<Ejercicios> ejercicios = ejerciciosService.obtenerEjerciciosPorSala(salaId);
        return ResponseEntity.ok(ejercicios);
    }

    @PostMapping
    public ResponseEntity<Ejercicios> crearEjercicio(@RequestBody @Valid Ejercicios ejercicio) {
        Ejercicios nuevoEjercicio = ejerciciosService.crearEjercicio(ejercicio);
        return ResponseEntity.ok(nuevoEjercicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ejercicios> actualizarEjercicio(@PathVariable Long id,
                                                           @RequestBody Ejercicios ejercicio) {
        Ejercicios ejercicioActualizado = ejerciciosService.actualizarEjercicio(id, ejercicio);
        return ResponseEntity.ok(ejercicioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEjercicio(@PathVariable Long id) {
        ejerciciosService.eliminarEjercicio(id);
        return ResponseEntity.noContent().build();
    }
}
