package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.entities.Plan;
import com.gestioneventos.cofira.repositories.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {
    private static final String PLAN_NO_ENCONTRADO = "Plan no encontrado con id ";

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<Plan> listarPlanes() {
        return planRepository.findAll();
    }

    public Plan obtenerPlan(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PLAN_NO_ENCONTRADO + id));
    }

    public Plan obtenerPlanPorUsuario(Long usuarioId) {
        return planRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado para usuario con id " + usuarioId));
    }

    public Plan crearPlan(Plan plan) {
        return planRepository.save(plan);
    }

    public Plan actualizarPlan(Long id, Plan planActualizado) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PLAN_NO_ENCONTRADO + id));

        if (planActualizado.getPrecio() != null) {
            plan.setPrecio(planActualizado.getPrecio());
        }
        if (planActualizado.getSubscripcionActiva() != null) {
            plan.setSubscripcionActiva(planActualizado.getSubscripcionActiva());
        }

        return planRepository.save(plan);
    }

    public void eliminarPlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PLAN_NO_ENCONTRADO + id));
        planRepository.delete(plan);
    }
}
