package daw2a.gestionbiblioteca.dto.usuario;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ModificarUsuarioDTO {
    private String nombre;

    @Email
    private String email;

    private String password;

    private String rol;
}
