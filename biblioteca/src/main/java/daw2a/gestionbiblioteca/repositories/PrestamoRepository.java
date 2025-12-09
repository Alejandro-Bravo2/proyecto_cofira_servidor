package daw2a.gestionbiblioteca.repositories;

import daw2a.gestionbiblioteca.entities.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    //Obtener préstamos de un usuario específico
    List<Prestamo> findByUsuarioId(Long usuarioId);

    // Obtener préstamos para un libro específico
    List<Prestamo> findByLibroId(Long libroId);


}
