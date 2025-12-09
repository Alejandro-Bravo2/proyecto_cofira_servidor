package daw2a.gestionbiblioteca.dto.libro;

import lombok.Data;

@Data
public class LibroDetalleDTO {
    private Long id;
    private String titulo;
    private String genero;
    private String estado;
    private String anyoPublicacion;
    private String nombreAutor;
}
