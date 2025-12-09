package com.gestioneventos.cofira.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Builder.Default
    private String rol = "PARTICIPANTE";

    private Integer edad;
    private Double peso;
    private Double altura;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Objetivos objetivos;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "sala_gimnasio_id")
    private SalaDeGimnasio salaDeGimnasio;

    @ManyToOne
    @JoinColumn(name = "alimento_id")
    private Alimento alimento;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) &&
                Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
