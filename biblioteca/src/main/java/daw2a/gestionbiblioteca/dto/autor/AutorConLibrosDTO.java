package daw2a.gestionbiblioteca.dto.autor;

import lombok.Data;

import java.util.List;

@Data
public class AutorConLibrosDTO { //DTO con libros
    private Long id;
    private String nombre;
    private String nacionalidad;
    private List<String> libros;
}