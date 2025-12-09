package daw2a.gestionbiblioteca.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@Builder
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;
    private String nacionalidad;

    @OneToMany(mappedBy = "autor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference  // Lado propietario de la relación
    private List<Libro> libros;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Autor autor)) return false;
        return Objects.equals(nombre, autor.nombre) && Objects.equals(nacionalidad, autor.nacionalidad) && Objects.equals(libros, autor.libros);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, nacionalidad, libros);
    }
}
