package daw2a.gestionbiblioteca.dto.prestamo;

import jakarta.validation.constraints.NotNull;

public class RenovarPrestamoDTO {
    @NotNull
    private int diasExtension;
}
