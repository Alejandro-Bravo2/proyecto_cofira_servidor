package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.dto.rutinaalimentacion.RutinaAlimentacionDTO;
import com.gestioneventos.cofira.dto.rutinaejercicio.RutinaEjercicioDTO;
import com.gestioneventos.cofira.dto.usuario.CrearUsuarioDTO;
import com.gestioneventos.cofira.dto.usuario.ModificarUsuarioDTO;
import com.gestioneventos.cofira.dto.usuario.UsuarioDetalleDTO;
import com.gestioneventos.cofira.dto.usuario.UsuarioListadoDTO;
import com.gestioneventos.cofira.entities.RutinaAlimentacion;
import com.gestioneventos.cofira.entities.RutinaEjercicio;
import com.gestioneventos.cofira.entities.Usuario;
import com.gestioneventos.cofira.enums.Rol;
import com.gestioneventos.cofira.exceptions.RecursoDuplicadoException;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RutinaAlimentacionService rutinaAlimentacionService;

    @Mock
    private RutinaEjercicioService rutinaEjercicioService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario1;
    private Usuario usuario2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        RutinaAlimentacion rutinaAlimentacion = new RutinaAlimentacion();
        rutinaAlimentacion.setId(10L);
        RutinaAlimentacionDTO rutinaAlimentacionDTO = new RutinaAlimentacionDTO();
        rutinaAlimentacionDTO.setId(10L);

        RutinaEjercicio rutinaEjercicio = new RutinaEjercicio();
        rutinaEjercicio.setId(20L);
        RutinaEjercicioDTO rutinaEjercicioDTO = new RutinaEjercicioDTO();
        rutinaEjercicioDTO.setId(20L);

        when(rutinaAlimentacionService.convertirADTO(rutinaAlimentacion)).thenReturn(rutinaAlimentacionDTO);
        when(rutinaEjercicioService.convertirADTO(rutinaEjercicio)).thenReturn(rutinaEjercicioDTO);

        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Alice Smith");
        usuario1.setUsername("alice");
        usuario1.setEmail("alice@example.com");
        usuario1.setPassword("pass123");
        usuario1.setRol(Rol.USER);
        usuario1.setEdad(25);
        usuario1.setPeso(60.5);
        usuario1.setAltura(165.0);
        usuario1.setAlimentosFavoritos(Arrays.asList("Manzana", "Pollo"));
        usuario1.setAlergias(Collections.singletonList("Nueces"));
        usuario1.setRutinaAlimentacion(rutinaAlimentacion);
        usuario1.setRutinaEjercicio(rutinaEjercicio);

        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Bob Johnson");
        usuario2.setUsername("bob");
        usuario2.setEmail("bob@example.com");
        usuario2.setPassword("pass456");
        usuario2.setRol(Rol.ADMIN);
        usuario2.setEdad(30);
        usuario2.setPeso(75.0);
        usuario2.setAltura(180.0);
        usuario2.setAlimentosFavoritos(Collections.emptyList());
        usuario2.setAlergias(Collections.emptyList());

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testListarUsuarios_NoFilter() {
        Page<Usuario> usuariosPage = new PageImpl<>(Arrays.asList(usuario1, usuario2));
        when(usuarioRepository.findAll(pageable)).thenReturn(usuariosPage);

        Page<UsuarioListadoDTO> result = usuarioService.listarUsuarios(null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Alice Smith", result.getContent().get(0).getNombre());
        assertEquals("Bob Johnson", result.getContent().get(1).getNombre());
        verify(usuarioRepository, times(1)).findAll(pageable);
        verify(usuarioRepository, never()).findByNombreContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void testListarUsuarios_WithNameFilter() {
        Page<Usuario> usuariosPage = new PageImpl<>(Collections.singletonList(usuario1));
        when(usuarioRepository.findByNombreContainingIgnoreCase("alice", pageable)).thenReturn(usuariosPage);

        Page<UsuarioListadoDTO> result = usuarioService.listarUsuarios("alice", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Alice Smith", result.getContent().get(0).getNombre());
        verify(usuarioRepository, times(1)).findByNombreContainingIgnoreCase("alice", pageable);
        verify(usuarioRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void testListarUsuarios_Empty() {
        Page<Usuario> emptyPage = new PageImpl<>(Collections.emptyList());
        when(usuarioRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UsuarioListadoDTO> result = usuarioService.listarUsuarios(null, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(usuarioRepository, times(1)).findAll(pageable);
    }

    @Test
    void testObtenerUsuario_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));

        UsuarioDetalleDTO result = usuarioService.obtenerUsuario(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice Smith", result.getNombre());
        assertNotNull(result.getRutinaAlimentacion());
        assertNotNull(result.getRutinaEjercicio());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerUsuario_NotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            usuarioService.obtenerUsuario(99L);
        });

        assertEquals("Usuario no encontrado con id 99", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    void testObtenerUsuarioByEmail_Success() {
        when(usuarioRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(usuario1));

        UsuarioDetalleDTO result = usuarioService.obtenerUsuarioByEmail("alice@example.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice Smith", result.getNombre());
        verify(usuarioRepository, times(1)).findByEmail("alice@example.com");
    }

    @Test
    void testObtenerUsuarioByEmail_NotFound() {
        when(usuarioRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            usuarioService.obtenerUsuarioByEmail("nonexistent@example.com");
        });

        assertEquals("Usuario no encontrado con email nonexistent@example.com", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testObtenerUsuarioByUsername_Success() {
        when(usuarioRepository.findByUsername("alice")).thenReturn(Optional.of(usuario1));

        UsuarioDetalleDTO result = usuarioService.obtenerUsuarioByUsername("alice");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice Smith", result.getNombre());
        verify(usuarioRepository, times(1)).findByUsername("alice");
    }

    @Test
    void testObtenerUsuarioByUsername_NotFound() {
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            usuarioService.obtenerUsuarioByUsername("nonexistent");
        });

        assertEquals("Usuario no encontrado con username nonexistent", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testCrearUsuario_Success() {
        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setNombre("Carlos Diaz");
        crearUsuarioDTO.setUsername("carlosd");
        crearUsuarioDTO.setEmail("carlos@example.com");
        crearUsuarioDTO.setPassword("newpass");
        crearUsuarioDTO.setRol(Rol.USER);
        crearUsuarioDTO.setEdad(28);
        crearUsuarioDTO.setPeso(70.0);
        crearUsuarioDTO.setAltura(170.0);
        crearUsuarioDTO.setAlimentosFavoritos(Arrays.asList("Pasta"));
        crearUsuarioDTO.setAlergias(Collections.emptyList());

        Usuario newUsuario = new Usuario();
        newUsuario.setId(3L);
        newUsuario.setNombre(crearUsuarioDTO.getNombre());
        newUsuario.setUsername(crearUsuarioDTO.getUsername());
        newUsuario.setEmail(crearUsuarioDTO.getEmail());
        newUsuario.setPassword(crearUsuarioDTO.getPassword());
        newUsuario.setRol(crearUsuarioDTO.getRol());
        newUsuario.setEdad(crearUsuarioDTO.getEdad());
        newUsuario.setPeso(crearUsuarioDTO.getPeso());
        newUsuario.setAltura(crearUsuarioDTO.getAltura());
        newUsuario.setAlimentosFavoritos(crearUsuarioDTO.getAlimentosFavoritos());
        newUsuario.setAlergias(crearUsuarioDTO.getAlergias());

        when(usuarioRepository.findByEmail(crearUsuarioDTO.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(crearUsuarioDTO.getUsername())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(newUsuario);

        UsuarioDetalleDTO result = usuarioService.crearUsuario(crearUsuarioDTO);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Carlos Diaz", result.getNombre());
        assertEquals("carlosd", result.getUsername());
        verify(usuarioRepository, times(1)).findByEmail(crearUsuarioDTO.getEmail());
        verify(usuarioRepository, times(1)).findByUsername(crearUsuarioDTO.getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_DuplicateEmail() {
        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setEmail("alice@example.com");

        when(usuarioRepository.findByEmail(crearUsuarioDTO.getEmail())).thenReturn(Optional.of(usuario1));

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class, () -> {
            usuarioService.crearUsuario(crearUsuarioDTO);
        });

        assertEquals("El email alice@example.com ya está en uso.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(crearUsuarioDTO.getEmail());
        verify(usuarioRepository, never()).findByUsername(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_DuplicateUsername() {
        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setEmail("unique@example.com");
        crearUsuarioDTO.setUsername("alice");

        when(usuarioRepository.findByEmail(crearUsuarioDTO.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(crearUsuarioDTO.getUsername())).thenReturn(Optional.of(usuario1));

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class, () -> {
            usuarioService.crearUsuario(crearUsuarioDTO);
        });

        assertEquals("El username alice ya está en uso.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(crearUsuarioDTO.getEmail());
        verify(usuarioRepository, times(1)).findByUsername(crearUsuarioDTO.getUsername());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_FullUpdateSuccess() {
        ModificarUsuarioDTO modificarUsuarioDTO = new ModificarUsuarioDTO();
        modificarUsuarioDTO.setNombre("Alicia Smith");
        modificarUsuarioDTO.setEmail("alicia.smith@example.com");
        modificarUsuarioDTO.setPassword("newSecurePass");
        modificarUsuarioDTO.setEdad(26);
        modificarUsuarioDTO.setPeso(62.0);
        modificarUsuarioDTO.setAltura(166.0);
        modificarUsuarioDTO.setAlimentosFavoritos(Arrays.asList("Arroz", "Pescado"));
        modificarUsuarioDTO.setAlergias(Arrays.asList("Gluten"));

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setId(1L);
        updatedUsuario.setNombre(modificarUsuarioDTO.getNombre());
        updatedUsuario.setUsername("alice"); // Username should not change from DTO
        updatedUsuario.setEmail(modificarUsuarioDTO.getEmail());
        updatedUsuario.setPassword(modificarUsuarioDTO.getPassword());
        updatedUsuario.setRol(Rol.USER); // Rol should not change from DTO
        updatedUsuario.setEdad(modificarUsuarioDTO.getEdad());
        updatedUsuario.setPeso(modificarUsuarioDTO.getPeso());
        updatedUsuario.setAltura(modificarUsuarioDTO.getAltura());
        updatedUsuario.setAlimentosFavoritos(modificarUsuarioDTO.getAlimentosFavoritos());
        updatedUsuario.setAlergias(modificarUsuarioDTO.getAlergias());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        UsuarioDetalleDTO result = usuarioService.actualizarUsuario(1L, modificarUsuarioDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alicia Smith", result.getNombre());
        assertEquals("alicia.smith@example.com", result.getEmail());
        assertEquals(26, result.getEdad());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_PartialUpdate() {
        ModificarUsuarioDTO modificarUsuarioDTO = new ModificarUsuarioDTO();
        modificarUsuarioDTO.setPeso(61.0); // Only update weight

        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);
        existingUsuario.setNombre("Alice Smith");
        existingUsuario.setPeso(60.5); // Original weight

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setId(1L);
        updatedUsuario.setNombre("Alice Smith");
        updatedUsuario.setPeso(61.0); // Updated weight

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        UsuarioDetalleDTO result = usuarioService.actualizarUsuario(1L, modificarUsuarioDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice Smith", result.getNombre());
        assertEquals(61.0, result.getPeso());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_NotFound() {
        ModificarUsuarioDTO modificarUsuarioDTO = new ModificarUsuarioDTO();
        modificarUsuarioDTO.setNombre("Non Existent");

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            usuarioService.actualizarUsuario(99L, modificarUsuarioDTO);
        });

        assertEquals("Usuario no encontrado con id 99", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        doNothing().when(usuarioRepository).delete(usuario1);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).delete(usuario1);
    }

    @Test
    void testEliminarUsuario_NotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            usuarioService.eliminarUsuario(99L);
        });

        assertEquals("Usuario no encontrado con id 99", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void testObtenerUsuarioPorId_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));

        Usuario result = usuarioService.obtenerUsuarioPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice Smith", result.getNombre());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerUsuarioPorId_NotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            usuarioService.obtenerUsuarioPorId(99L);
        });

        assertEquals("Usuario no encontrado con id 99", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    void testObtenerUsuariosPorRangoEdad_Found() {
        List<Usuario> usersInAgeRange = Arrays.asList(usuario1, usuario2);
        when(usuarioRepository.findByEdadBetween(20, 35)).thenReturn(usersInAgeRange);

        List<Usuario> result = usuarioService.obtenerUsuariosPorRangoEdad(20, 35);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice Smith", result.get(0).getNombre());
        assertEquals("Bob Johnson", result.get(1).getNombre());
        verify(usuarioRepository, times(1)).findByEdadBetween(20, 35);
    }

    @Test
    void testObtenerUsuariosPorRangoEdad_NotFound() {
        when(usuarioRepository.findByEdadBetween(40, 50)).thenReturn(Collections.emptyList());

        List<Usuario> result = usuarioService.obtenerUsuariosPorRangoEdad(40, 50);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(usuarioRepository, times(1)).findByEdadBetween(40, 50);
    }

    @Test
    void testObtenerUsuariosConPlanActivo_Found() {
        List<Usuario> usersWithActivePlan = Collections.singletonList(usuario1);
        when(usuarioRepository.findUsuariosConPlanActivo()).thenReturn(usersWithActivePlan);

        List<Usuario> result = usuarioService.obtenerUsuariosConPlanActivo();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Alice Smith", result.get(0).getNombre());
        verify(usuarioRepository, times(1)).findUsuariosConPlanActivo();
    }

    @Test
    void testObtenerUsuariosConPlanActivo_NotFound() {
        when(usuarioRepository.findUsuariosConPlanActivo()).thenReturn(Collections.emptyList());

        List<Usuario> result = usuarioService.obtenerUsuariosConPlanActivo();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(usuarioRepository, times(1)).findUsuariosConPlanActivo();
    }




}
