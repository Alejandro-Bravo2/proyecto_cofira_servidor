package com.gestioneventos.cofira.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "alimentos_favoritos", joinColumns = @JoinColumn(name = "alimento_id"))
    @Column(name = "alimento_favorito")
    private List<String> alimentosFavoritos;

    @ElementCollection
    @CollectionTable(name = "lista_alergias", joinColumns = @JoinColumn(name = "alimento_id"))
    @Column(name = "alergia")
    private List<String> listaAlergias;

    @OneToMany(mappedBy = "alimento")
    private List<Usuario> usuarios;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alimento alimento)) return false;
        return Objects.equals(id, alimento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
