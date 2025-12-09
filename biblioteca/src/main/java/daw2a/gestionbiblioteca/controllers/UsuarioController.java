package daw2a.gestionbiblioteca.controllers;

import daw2a.gestionbiblioteca.dto.usuario.CrearUsuarioDTO;
import daw2a.gestionbiblioteca.dto.usuario.ModificarUsuarioDTO;
import daw2a.gestionbiblioteca.dto.usuario.UsuarioDetalleDTO;
import daw2a.gestionbiblioteca.dto.usuario.UsuarioListadoDTO;
import daw2a.gestionbiblioteca.entities.Usuario;
import daw2a.gestionbiblioteca.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
    }

    /**
     * Listar todos los usuarios, con soporte para paginación y filtrado opcional por nombre.
     * Acceso permitido solo para usuarios con rol BIBLIOTECARIO.
     */
    @GetMapping
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<Page<UsuarioListadoDTO>> listarUsuarios(@RequestParam(required = false) String nombre,
                                                                  Pageable pageable) {
        Page<UsuarioListadoDTO> usuarios = usuarioService.listarUsuarios(nombre, pageable);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Obtener los detalles de un usuario específico por su ID.
     * Acceso permitido solo para usuarios con rol BIBLIOTECARIO.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<UsuarioDetalleDTO> obtenerUsuario(@PathVariable Long id) {
        UsuarioDetalleDTO usuario = usuarioService.obtenerUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Obtener los detalles de un usuario específico por su email.
     * Acceso permitido solo para usuarios con rol BIBLIOTECARIO.
     */
    @GetMapping("/email")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<UsuarioDetalleDTO> obtenerUsuarioPorEmail(@RequestParam("email") String email) {
        UsuarioDetalleDTO usuario = usuarioService.obtenerUsuarioByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Crear un nuevo usuario a partir de un DTO.
     * Acceso permitido solo para usuarios con rol BIBLIOTECARIO.
     */
    @PostMapping
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<UsuarioDetalleDTO> crearUsuario(@RequestBody @Valid CrearUsuarioDTO crearUsuarioDTO) {
        UsuarioDetalleDTO nuevoUsuario = usuarioService.crearUsuario(crearUsuarioDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    /**
     * Obtener los detalles del usuario actualmente autenticado.
     * Acceso permitido para cualquier usuario autenticado.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> obtenerMiPerfil() {
        Usuario usuario = usuarioService.obtenerMiPerfil();
        return ResponseEntity.ok(usuario);
    }

    /**
     * Actualizar los detalles de un usuario específico por su ID.
     * Acceso permitido solo para usuarios con rol BIBLIOTECARIO.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<UsuarioDetalleDTO> actualizarUsuario(@PathVariable Long id,
                                                               @RequestBody ModificarUsuarioDTO modificarUsuarioDTO) {
        UsuarioDetalleDTO usuario = usuarioService.actualizarUsuario(id, modificarUsuarioDTO);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Cambiar el rol de un usuario por su ID.
     * Solo bibliotecarios pueden cambiar roles.
     */
    @PutMapping("/{id}/cambiar-rol")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<UsuarioDetalleDTO> cambiarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        UsuarioDetalleDTO usuarioActualizado = usuarioService.cambiarRol(id, nuevoRol);
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     * Subir un avatar para un usuario específico por su ID.
     * El archivo debe ser una imagen válida y cumplir con las restricciones de tamaño.
     */
    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> cargarAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            usuarioService.guardarAvatar(id, file);
            return ResponseEntity.ok("Avatar actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al cargar el avatar: " + e.getMessage());
        }
    }

    /**
     * Obtener el avatar del usuario actualmente autenticado.
     * El archivo es devuelto con el tipo MIME correspondiente (JPEG, PNG o GIF).
     */
    @GetMapping("/me/avatar")
    public ResponseEntity<Resource> obtenerAvatarUsuarioLogueado() {
        Resource avatar = usuarioService.obtenerAvatarGenerico(null);
        try {
            Path avatarPath = Paths.get(avatar.getURL().getPath());
            MediaType mediaType = determinarMediaType(avatarPath);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"" + avatar.getFilename() + "\"")
                    .contentType(mediaType)
                    .body(avatar);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar el archivo de avatar", e);
        }
    }

    /**
     * Obtener el avatar de un usuario específico por su ID.
     * Devuelve el archivo con el tipo MIME correspondiente.
     */
    @GetMapping("/{id}/avatar")
    public ResponseEntity<Resource> obtenerAvatar(@PathVariable Long id) {
        Resource avatar = usuarioService.obtenerAvatarGenerico(id);
        try {
            Path avatarPath = Paths.get(avatar.getURL().getPath());
            MediaType mediaType = determinarMediaType(avatarPath);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"" + avatar.getFilename() + "\"")
                    .contentType(mediaType)
                    .body(avatar);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar el archivo de avatar", e);
        }
    }

    /**
     * Eliminar un usuario por su ID.
     * Acceso permitido solo para usuarios con rol BIBLIOTECARIO.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Determinar el tipo de contenido (MIME type) de un archivo.
     * Solo permite tipos de imagen válidos (JPEG, PNG, GIF).
     */
    private MediaType determinarMediaType(Path ficheroPath) {
        try {
            String contentType = Files.probeContentType(ficheroPath);
            if (contentType == null || contentType.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de contenido no puede ser nulo o vacío.");
            }
            return switch (contentType.toLowerCase()) {
                case "image/jpeg" -> MediaType.IMAGE_JPEG;
                case "image/png" -> MediaType.IMAGE_PNG;
                case "image/gif" -> MediaType.IMAGE_GIF;
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de imagen no soportado: " + contentType);
            };
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al determinar el tipo de contenido", e);
        }
    }
}