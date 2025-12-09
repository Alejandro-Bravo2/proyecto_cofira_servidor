package com.gestioneventos.cofira.controllers;

import com.gestioneventos.cofira.entities.Objetivos;
import com.gestioneventos.cofira.services.ObjetivosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/objetivos")
public class ObjetivosController {

    private final ObjetivosService objetivosService;

    @Autowired
    public ObjetivosController(ObjetivosService objetivosService) {
        this.objetivosService = objetivosService;
    }

    @GetMapping
    public ResponseEntity<List<Objetivos>> listarObjetivos() {
        List<Objetivos> objetivos = objetivosService.listarObjetivos();
        return ResponseEntity.ok(objetivos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Objetivos> obtenerObjetivos(@PathVariable Long id) {
        Objetivos objetivos = objetivosService.obtenerObjetivos(id);
        return ResponseEntity.ok(objetivos);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Objetivos> obtenerObjetivosPorUsuario(@PathVariable Long usuarioId) {
        Objetivos objetivos = objetivosService.obtenerObjetivosPorUsuario(usuarioId);
        return ResponseEntity.ok(objetivos);
    }

    @PostMapping
    public ResponseEntity<Objetivos> crearObjetivos(@RequestBody @Valid Objetivos objetivos) {
        Objetivos nuevosObjetivos = objetivosService.crearObjetivos(objetivos);
        return ResponseEntity.ok(nuevosObjetivos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Objetivos> actualizarObjetivos(@PathVariable Long id,
                                                          @RequestBody Objetivos objetivos) {
        Objetivos objetivosActualizados = objetivosService.actualizarObjetivos(id, objetivos);
        return ResponseEntity.ok(objetivosActualizados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarObjetivos(@PathVariable Long id) {
        objetivosService.eliminarObjetivos(id);
        return ResponseEntity.noContent().build();
    }
}
