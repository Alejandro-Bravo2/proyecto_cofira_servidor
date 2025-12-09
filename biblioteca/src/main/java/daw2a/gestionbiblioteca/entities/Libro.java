package daw2a.gestionbiblioteca.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
//@EqualsAndHashCode(exclude = "id") // Por si no queremos que el id se tenga en cuenta en equals y hashcode
@Getter
@Setter
@ToString
@AllArgsConstructor @NoArgsConstructor
@Builder
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String genero;
    private String anyoPublicacion;
    private String estado; // Disponible, Prestado, Retraso

    @ManyToOne
    @JoinColumn(name = "autor_id")
    @JsonBackReference // Lado no propietario de la relación, lado inverso de la relacilón
    private Autor autor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libro libro)) return false;
        return Objects.equals(titulo, libro.titulo) && Objects.equals(genero, libro.genero)
                && Objects.equals(anyoPublicacion, libro.anyoPublicacion)
                && Objects.equals(estado, libro.estado)
                && Objects.equals(autor, libro.autor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, genero, anyoPublicacion, estado, autor);
    }
}
