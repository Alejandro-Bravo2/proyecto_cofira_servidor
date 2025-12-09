package daw2a.gestionbiblioteca.dto.autor;

import lombok.Data;

@Data
public class AutorDTO { // DTO = Data Transfer Object, sin libros
    private Long id;
    private String nombre;
    private String nacionalidad;

}