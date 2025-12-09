package daw2a.gestionbiblioteca.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class FileService {

    // Ruta base para almacenar los avatares de los usuarios
    private final Path basePath = Paths.get("uploads/usuario");

    public FileService() throws IOException {
        // Crear el directorio base si no existe
        Files.createDirectories(basePath);
    }

    /**
     * Guarda un fichero para un usuario específico.
     *
     * @param usuarioId ID del usuario asociado al fichero.
     * @param fichero El fichero que se debe guardar.
     * @return La ruta absoluta del fichero guardado.
     * @throws IOException Si ocurre un error durante el guardado del fichero.
     */
    public String guardarFichero(Long usuarioId, MultipartFile fichero) throws IOException {
        // Validar tipo y tamaño del fichero
        validarTipoDeFichero(fichero);
        validarTamanoFichero(fichero);

        // Usar un nombre por defecto si el original es nulo o vacío
        String originalFilename = fichero.getOriginalFilename();
        String filename = (originalFilename == null || originalFilename.isBlank()) ? "archivo_por_defecto" : originalFilename;

        // Crear directorio específico para el usuario si no existe
        Path userDir = basePath.resolve(String.valueOf(usuarioId));
        Files.createDirectories(userDir);

        // Guardar el fichero en el directorio del usuario
        Path rutaFichero = userDir.resolve(filename);
        Files.copy(fichero.getInputStream(), rutaFichero, StandardCopyOption.REPLACE_EXISTING);

        return rutaFichero.toString();
    }

    /**
     * Carga un fichero a partir de su ruta.
     *
     * @param ruta Ruta del fichero a cargar.
     * @return El fichero como un recurso de Spring.
     * @throws RuntimeException Si el fichero no existe o ocurre un error al cargarlo.
     */
    public Resource cargarFichero(String ruta) {
        try {
            // Resolver la ruta del fichero
            Path ficheroPath = Paths.get(ruta);

            // Verificar que el fichero existe
            if (!Files.exists(ficheroPath)) {
                throw new NoSuchFileException("El fichero no existe: " + ruta);
            }

            // Devolver el recurso
            return new UrlResource(ficheroPath.toUri());
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el fichero: " + ruta, e);
        }
    }

    /**
     * Valida que el fichero tenga un tipo de contenido permitido.
     *
     * @param fichero El fichero a validar.
     * @throws IllegalArgumentException Si el tipo de fichero no está permitido.
     */
    private void validarTipoDeFichero(MultipartFile fichero) {
        String contentType = fichero.getContentType();
        if (!List.of("image/jpeg", "image/png", "image/gif", "image/webp").contains(contentType)) {
            throw new IllegalArgumentException("Formato de fichero no permitido. Solo JPG, PNG, GIF, y WEBP.");
        }
    }

    /**
     * Valida que el tamaño del fichero no supere el límite establecido.
     *
     * @param fichero El fichero a validar.
     * @throws IllegalArgumentException Si el tamaño del fichero supera el límite.
     */
    private void validarTamanoFichero(MultipartFile fichero) {
        if (fichero.getSize() > 2 * 1024 * 1024) { // 2 MB
            throw new IllegalArgumentException("El fichero es demasiado grande. Tamaño máximo permitido: 2 MB.");
        }
    }
}