package daw2a.gestionbiblioteca.dto.libro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearLibroDTO {
    @NotNull
    @Size(min = 1, max = 100)
    private String titulo;

    @NotNull
    private String genero;
    private Long autorId;
    private String anyoPublicacion;
}
