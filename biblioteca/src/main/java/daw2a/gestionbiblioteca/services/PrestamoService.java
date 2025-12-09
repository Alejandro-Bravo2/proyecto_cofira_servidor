package daw2a.gestionbiblioteca.services;

import daw2a.gestionbiblioteca.dto.prestamo.CrearPrestamoDTO;
import daw2a.gestionbiblioteca.dto.prestamo.PrestamoDTO;
import daw2a.gestionbiblioteca.entities.Libro;
import daw2a.gestionbiblioteca.entities.Prestamo;
import daw2a.gestionbiblioteca.entities.Usuario;
import daw2a.gestionbiblioteca.exceptions.LibroNoDisponibleException;
import daw2a.gestionbiblioteca.exceptions.PrestamoVencidoException;
import daw2a.gestionbiblioteca.exceptions.RecursoNoEncontradoException;
import daw2a.gestionbiblioteca.repositories.LibroRepository;
import daw2a.gestionbiblioteca.repositories.PrestamoRepository;
import daw2a.gestionbiblioteca.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, LibroRepository libroRepository, UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<PrestamoDTO> listarPrestamos(Pageable pageable) {
        return prestamoRepository.findAll(pageable).map(this::convertirAPrestamoDTO);
    }

    public PrestamoDTO obtenerPrestamoPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));
        return convertirAPrestamoDTO(prestamo);
    }

    @Transactional
    public PrestamoDTO crearPrestamo(CrearPrestamoDTO prestamoCrearDTO) {
        Libro libro = libroRepository.findById(prestamoCrearDTO.getLibroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id: " + prestamoCrearDTO.getLibroId()));

        Usuario usuario = usuarioRepository.findById(prestamoCrearDTO.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + prestamoCrearDTO.getUsuarioId()));

        if ("prestado".equalsIgnoreCase(libro.getEstado())) {
            throw new LibroNoDisponibleException("El libro ya está prestado.");
        }

        if (tienePrestamosVencidos(usuario.getId())) {
            throw new PrestamoVencidoException("El usuario tiene préstamos vencidos.");
        }

        libro.setEstado("prestado");
        Prestamo prestamo = Prestamo.builder()
                .libro(libro)
                .usuario(usuario)
                .fechaPrestamo(LocalDate.now())
                .build();

        prestamoRepository.save(prestamo);
        return convertirAPrestamoDTO(prestamo);
    }

    public boolean tienePrestamosVencidos(Long usuarioId) {
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
        return prestamos.stream().anyMatch(Prestamo::estaVencido);
    }

    public PrestamoDTO actualizarPrestamo(Long id, CrearPrestamoDTO prestamoActualizadoDTO) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));

        Libro libro = libroRepository.findById(prestamoActualizadoDTO.getLibroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id: " + prestamoActualizadoDTO.getLibroId()));

        Usuario usuario = usuarioRepository.findById(prestamoActualizadoDTO.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + prestamoActualizadoDTO.getUsuarioId()));

        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoDTO renovarPrestamo(Long prestamoId, int diasExtension) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + prestamoId));

        if (prestamo.estaVencido()) {
            throw new PrestamoVencidoException("No se puede renovar un préstamo vencido.");
        }

        prestamo.setFechaPrestamo(prestamo.getFechaPrestamo().plusDays(diasExtension));
        return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoDTO devolverPrestamo(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));

        prestamo.getLibro().setEstado("disponible");
        prestamo.setFechaDevolucion(LocalDate.now());
        return convertirAPrestamoDTO(prestamoRepository.save(prestamo));
    }

    public void eliminarPrestamo(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id " + id));
        prestamo.getLibro().setEstado("disponible");
        prestamoRepository.delete(prestamo);
    }

    private PrestamoDTO convertirAPrestamoDTO(Prestamo prestamo) {
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(prestamo.getId());
        dto.setLibroId(prestamo.getLibro().getId());
        dto.setLibroTitulo(prestamo.getLibro().getTitulo());
        dto.setUsuarioId(prestamo.getUsuario().getId());
        dto.setNombre(prestamo.getUsuario().getNombre());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucion(prestamo.getFechaDevolucion());
        return dto;
    }
}