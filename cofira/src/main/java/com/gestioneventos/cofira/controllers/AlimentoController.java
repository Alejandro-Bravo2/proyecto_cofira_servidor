package com.gestioneventos.cofira.controllers;

import com.gestioneventos.cofira.entities.Alimento;
import com.gestioneventos.cofira.services.AlimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alimentos")
public class AlimentoController {

    private final AlimentoService alimentoService;

    @Autowired
    public AlimentoController(AlimentoService alimentoService) {
        this.alimentoService = alimentoService;
    }

    @GetMapping
    public ResponseEntity<List<Alimento>> listarAlimentos() {
        List<Alimento> alimentos = alimentoService.listarAlimentos();
        return ResponseEntity.ok(alimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alimento> obtenerAlimento(@PathVariable Long id) {
        Alimento alimento = alimentoService.obtenerAlimento(id);
        return ResponseEntity.ok(alimento);
    }

    @PostMapping
    public ResponseEntity<Alimento> crearAlimento(@RequestBody @Valid Alimento alimento) {
        Alimento nuevoAlimento = alimentoService.crearAlimento(alimento);
        return ResponseEntity.ok(nuevoAlimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alimento> actualizarAlimento(@PathVariable Long id,
                                                        @RequestBody Alimento alimento) {
        Alimento alimentoActualizado = alimentoService.actualizarAlimento(id, alimento);
        return ResponseEntity.ok(alimentoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAlimento(@PathVariable Long id) {
        alimentoService.eliminarAlimento(id);
        return ResponseEntity.noContent().build();
    }
}
