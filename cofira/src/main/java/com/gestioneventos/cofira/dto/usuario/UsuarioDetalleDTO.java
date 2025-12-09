package com.gestioneventos.cofira.dto.usuario;

import lombok.Data;

@Data
public class UsuarioDetalleDTO {
    private Long id;
    private String nombre;
    private String username;
    private String email;
    private String rol;
    private Integer edad;
    private Double peso;
    private Double altura;
}
