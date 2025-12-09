package daw2a.gestionbiblioteca.repositories;

import daw2a.gestionbiblioteca.entities.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository  extends JpaRepository<Autor,Long> {
    // Aquí podemos agregar métodos de consulta personalizados si fuera necesario
    Page<Autor> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
