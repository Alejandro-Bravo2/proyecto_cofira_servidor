package daw2a.gestionbiblioteca.repositories;

import daw2a.gestionbiblioteca.entities.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    //Búsqueda por título
    Page<Libro> findLibrosByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // Búsqueda por género
    Page<Libro> findByGeneroIgnoreCase(String genero, Pageable pageable);

    // Búsqueda por título y género
    Page<Libro> findByTituloContainingIgnoreCaseAndGeneroIgnoreCase(String titulo, String genero, Pageable pageable);

}
