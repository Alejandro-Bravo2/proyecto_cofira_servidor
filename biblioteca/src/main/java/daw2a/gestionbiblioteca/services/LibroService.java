package daw2a.gestionbiblioteca.services;

import daw2a.gestionbiblioteca.dto.libro.CrearLibroDTO;
import daw2a.gestionbiblioteca.dto.libro.ModificarLibroDTO;
import daw2a.gestionbiblioteca.dto.libro.LibroDetalleDTO;
import daw2a.gestionbiblioteca.dto.libro.LibroListadoDTO;
import daw2a.gestionbiblioteca.entities.Libro;
import daw2a.gestionbiblioteca.exceptions.RecursoNoEncontradoException;
import daw2a.gestionbiblioteca.repositories.AutorRepository;
import daw2a.gestionbiblioteca.repositories.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    @Autowired
    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public Page<LibroListadoDTO> listarLibros(String titulo, String genero, Pageable pageable) {
        Page<Libro> libros;
        if (titulo != null && genero != null) {
            libros = libroRepository.findByTituloContainingIgnoreCaseAndGeneroIgnoreCase(titulo, genero, pageable);
        } else if (titulo != null) {
            libros = libroRepository.findLibrosByTituloContainingIgnoreCase(titulo, pageable);
        } else if (genero != null) {
            libros = libroRepository.findByGeneroIgnoreCase(genero, pageable);
        } else {
            libros = libroRepository.findAll(pageable);
        }
        return libros.map(this::convertirALibroListadoDTO);
    }

    public LibroDetalleDTO obtenerLibro(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id " + id));
        return convertirALibroDetalleDTO(libro);
    }

    public LibroDetalleDTO crearLibro(CrearLibroDTO crearLibroDTO) {
        Libro libro = new Libro();
        libro.setTitulo(crearLibroDTO.getTitulo());
        libro.setGenero(crearLibroDTO.getGenero());
        libro.setAnyoPublicacion(crearLibroDTO.getAnyoPublicacion());
        // Establecer el estado a "disponible"
        libro.setEstado("disponible");
        libro.setAutor(autorRepository.findById(crearLibroDTO.getAutorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id: " + crearLibroDTO.getAutorId())));

        Libro nuevoLibro = libroRepository.save(libro);
        return convertirALibroDetalleDTO(nuevoLibro);
    }

    public LibroDetalleDTO actualizarLibro(Long id, ModificarLibroDTO modificarLibroDTO) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id " + id));

        if (modificarLibroDTO.getTitulo() != null) libro.setTitulo(modificarLibroDTO.getTitulo());
        if (modificarLibroDTO.getGenero() != null) libro.setGenero(modificarLibroDTO.getGenero());
        if (modificarLibroDTO.getAnyoPublicacion() != null) libro.setAnyoPublicacion(modificarLibroDTO.getAnyoPublicacion());
        if (modificarLibroDTO.getAutorId() != null) {
            libro.setAutor(autorRepository.findById(modificarLibroDTO.getAutorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado con id: " + modificarLibroDTO.getAutorId())));
        }

        return convertirALibroDetalleDTO(libroRepository.save(libro));
    }

    public void borrarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Libro no encontrado con id " + id);
        }
        libroRepository.deleteById(id);
    }

    // Métodos de conversión
    private LibroDetalleDTO convertirALibroDetalleDTO(Libro libro) {
        LibroDetalleDTO dto = new LibroDetalleDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setGenero(libro.getGenero());
        dto.setEstado(libro.getEstado());
        dto.setAnyoPublicacion(libro.getAnyoPublicacion());
        dto.setNombreAutor(libro.getAutor().getNombre());
        return dto;
    }

    private LibroListadoDTO convertirALibroListadoDTO(Libro libro) {
        LibroListadoDTO dto = new LibroListadoDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setGenero(libro.getGenero());
        return dto;
    }
}