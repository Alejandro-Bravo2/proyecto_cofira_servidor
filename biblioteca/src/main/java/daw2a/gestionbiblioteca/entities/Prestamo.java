package daw2a.gestionbiblioteca.entities;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"libro_id", "usuario_id", "fechaPrestamo"}))
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "libro_id")
    @NotNull
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @NotNull
    private Usuario usuario;

    @NotNull
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    // Duración en días del préstamo
    private int duracionDias = 14; // Ejemplo: préstamo por 14 días

    // Método para verificar si el préstamo está vencido
    public boolean estaVencido() {
        return fechaDevolucion == null && fechaPrestamo.plusDays(duracionDias).isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prestamo prestamo)) return false;
        return Objects.equals(id, prestamo.id)
                && Objects.equals(libro, prestamo.libro)
                && Objects.equals(usuario, prestamo.usuario)
                && Objects.equals(fechaPrestamo, prestamo.fechaPrestamo)
                && Objects.equals(fechaDevolucion, prestamo.fechaDevolucion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, libro, usuario, fechaPrestamo, fechaDevolucion);
    }
}
