package daw2a.gestionbiblioteca.dto.libro;

import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class ModificarLibroDTO {

    @Size(min = 1, max = 100)
    private String titulo;
    private String genero;
    private String anyoPublicacion;
    private Long autorId;
}

