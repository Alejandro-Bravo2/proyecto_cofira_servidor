package daw2a.gestionbiblioteca.dto.prestamo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PrestamoDTO {
    private Long id;
    private Long libroId;
    private String libroTitulo;
    private Long usuarioId;
    private String nombre;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
}