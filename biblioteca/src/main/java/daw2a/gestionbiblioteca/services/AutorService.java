package daw2a.gestionbiblioteca.services;

import daw2a.gestionbiblioteca.dto.autor.AutorConLibrosDTO;
import daw2a.gestionbiblioteca.dto.autor.AutorDTO;
import daw2a.gestionbiblioteca.dto.autor.CrearAutorDTO;
import daw2a.gestionbiblioteca.dto.autor.ModificarAutorDTO;
import daw2a.gestionbiblioteca.entities.Autor;
import daw2a.gestionbiblioteca.entities.Libro;
import daw2a.gestionbiblioteca.exceptions.RecursoNoEncontradoException;
import daw2a.gestionbiblioteca.repositories.AutorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    // Implementar métodos de servicio
    // Listar todos los autores
    public Page<?> listarAutores(String nombre, boolean conLibros, Pageable pageable) {
        Page<Autor> autores;
        if (nombre != null && !nombre.isEmpty()) {
            autores = autorRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        } else {
            autores = autorRepository.findAll(pageable);
        }

        if (conLibros) {
            return autores.map(this::convertirAAutorConLibrosDTO);
        } else {
            return autores.map(this::convertirAAutorDTO);
        }
    }

    // Obtener los detalles de un autor específico, por id
    public AutorDTO obtenerAutor(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id " + id));
        return convertirAAutorDTO(autor);
    }

    // Crear un nuevo autor
    public AutorDTO crearAutor(CrearAutorDTO crearAutorDTO) {
        Autor autor = new Autor();
        autor.setNombre(crearAutorDTO.getNombre());
        autor.setNacionalidad(crearAutorDTO.getNacionalidad());
        return convertirAAutorDTO(autorRepository.save(autor));
    }

    // Actualizar un autor existente
    public AutorDTO actualizarAutor(Long id, ModificarAutorDTO modificarAutorDTO) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id " + id));

        if (modificarAutorDTO.getNombre() != null) {
            autor.setNombre(modificarAutorDTO.getNombre());
        }
        if (modificarAutorDTO.getNacionalidad() != null) {
            autor.setNacionalidad(modificarAutorDTO.getNacionalidad());
        }

        return convertirAAutorDTO(autorRepository.save(autor));
    }

    // Eliminar un autor
    public void eliminarAutor(Long id)  {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id " + id));
        autorRepository.delete(autor);

        /* alternativa
             public void eliminarAutor(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Autor no encontrado con id " + id);
        }
        autorRepository.deleteById(id);
    }
         */
    }

    private AutorDTO convertirAAutorDTO(Autor autor) {
        AutorDTO dto = new AutorDTO();
        dto.setId(autor.getId());
        dto.setNombre(autor.getNombre());
        dto.setNacionalidad(autor.getNacionalidad());
        return dto;
    }

    private AutorConLibrosDTO convertirAAutorConLibrosDTO(Autor autor) {
        AutorConLibrosDTO dto = new AutorConLibrosDTO();
        dto.setId(autor.getId());
        dto.setNombre(autor.getNombre());
        dto.setNacionalidad(autor.getNacionalidad());
        dto.setLibros(autor.getLibros().stream()
                .map(Libro::getTitulo)
                .collect(Collectors.toList()));
        return dto;
    }
}
