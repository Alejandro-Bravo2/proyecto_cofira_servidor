package daw2a.gestionbiblioteca.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CrearUsuarioDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull(message = "El rol no puede ser nulo.")
    @Pattern(regexp = "USUARIO|BIBLIOTECARIO", message = "El rol debe ser 'USUARIO' o 'BIBLIOTECARIO'.") // Valida valores válidos
    // valor por defecto es USUARIO
    private String rol = "USUARIO";

}
