package com.gestioneventos.cofira.dto.usuario;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ModificarUsuarioDTO {
    private String nombre;

    @Email(message = "El email debe ser válido")
    private String email;

    private String password;
    private Integer edad;
    private Double peso;
    private Double altura;
}
