package com.gestioneventos.cofira.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer series;

    @NotNull
    private Integer repeticiones;

    @NotNull
    private String nombreEjercicio;

    @ManyToOne
    @JoinColumn(name = "sala_gimnasio_id")
    @JsonBackReference
    private SalaDeGimnasio salaDeGimnasio;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ejercicios ejercicio)) return false;
        return Objects.equals(id, ejercicio.id) &&
                Objects.equals(nombreEjercicio, ejercicio.nombreEjercicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreEjercicio);
    }
}
