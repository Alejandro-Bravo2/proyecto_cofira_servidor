package com.gestioneventos.cofira.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String nombre;
    private String username;
    private String email;
    private String rol;
    private Integer edad;
    private Double peso;
    private Double altura;
}
