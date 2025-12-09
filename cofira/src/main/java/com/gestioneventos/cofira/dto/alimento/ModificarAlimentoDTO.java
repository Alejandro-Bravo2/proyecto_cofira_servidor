package com.gestioneventos.cofira.dto.alimento;

import lombok.Data;

import java.util.List;

@Data
public class ModificarAlimentoDTO {
    private List<String> alimentosFavoritos;
    private List<String> listaAlergias;
}
