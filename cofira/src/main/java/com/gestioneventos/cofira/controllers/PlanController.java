package com.gestioneventos.cofira.controllers;

import com.gestioneventos.cofira.entities.Plan;
import com.gestioneventos.cofira.services.PlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planes")
public class PlanController {

    private final PlanService planService;

    @Autowired
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes() {
        List<Plan> planes = planService.listarPlanes();
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        Plan plan = planService.obtenerPlan(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Plan> obtenerPlanPorUsuario(@PathVariable Long usuarioId) {
        Plan plan = planService.obtenerPlanPorUsuario(usuarioId);
        return ResponseEntity.ok(plan);
    }

    @PostMapping
    public ResponseEntity<Plan> crearPlan(@RequestBody @Valid Plan plan) {
        Plan nuevoPlan = planService.crearPlan(plan);
        return ResponseEntity.ok(nuevoPlan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plan> actualizarPlan(@PathVariable Long id,
                                                @RequestBody Plan plan) {
        Plan planActualizado = planService.actualizarPlan(id, plan);
        return ResponseEntity.ok(planActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPlan(@PathVariable Long id) {
        planService.eliminarPlan(id);
        return ResponseEntity.noContent().build();
    }
}
