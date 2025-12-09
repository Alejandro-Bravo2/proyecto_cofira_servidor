package daw2a.gestionbiblioteca.dto.autor;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearAutorDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String nacionalidad;

}