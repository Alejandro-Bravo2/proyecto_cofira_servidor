package daw2a.gestionbiblioteca.dto.usuario;

import lombok.Data;

@Data
public class UsuarioDetalleDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
}

