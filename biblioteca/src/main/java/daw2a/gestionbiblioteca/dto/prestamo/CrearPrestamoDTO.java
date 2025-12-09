package daw2a.gestionbiblioteca.dto.prestamo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearPrestamoDTO {
    @NotNull
    private Long libroId;

    @NotNull
    private Long usuarioId;
}