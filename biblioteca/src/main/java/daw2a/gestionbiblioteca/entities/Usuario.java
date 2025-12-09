package daw2a.gestionbiblioteca.entities;

import daw2a.gestionbiblioteca.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
//@Data
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @NotNull
    @Column(unique = true)
    @Email
    private String email;
    private String password;
   // @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Rol rol;
   // private Set<Rol> roles = new HashSet<>();

    // Avatar del usuario, imagen opcional, puede ser nulo
    @Column(nullable = true)
    private String avatar;

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
