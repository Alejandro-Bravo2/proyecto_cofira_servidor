package com.gestioneventos.cofira.services;

import com.gestioneventos.cofira.dto.auth.AuthResponseDTO;
import com.gestioneventos.cofira.dto.auth.LoginRequestDTO;
import com.gestioneventos.cofira.dto.auth.RegisterRequestDTO;
import com.gestioneventos.cofira.dto.auth.UserInfoDTO;
import com.gestioneventos.cofira.entities.TokenRevocado;
import com.gestioneventos.cofira.entities.Usuario;
import com.gestioneventos.cofira.enums.Rol;
import com.gestioneventos.cofira.exceptions.RecursoDuplicadoException;
import com.gestioneventos.cofira.exceptions.RecursoNoEncontradoException;
import com.gestioneventos.cofira.repositories.TokenRevocadoRepository;
import com.gestioneventos.cofira.repositories.UsuarioRepository;
import com.gestioneventos.cofira.security.JwtUtils;
import com.gestioneventos.cofira.security.UserDetailsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRevocadoRepository tokenRevocadoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Reset SecurityContextHolder before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLogin_Success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "encodedPassword", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockedJwtToken");

        AuthResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockedJwtToken", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(Rol.USER, response.getRol());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    void testRegister_Success() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setNombre("Test User");
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("newpassword");
        registerRequest.setRol(Rol.USER);

        Usuario newUser = Usuario.builder()
                .id(2L)
                .nombre("Test User")
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedNewPassword")
                .rol(Rol.USER)
                .build();

        // Mock passwordEncoder
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedNewPassword");
        // Mock usuarioRepository.existsByUsername and existsByEmail
        when(usuarioRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        // Mock usuarioRepository.save
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(newUser);

        // Mock login call after registration
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("newuser");
        loginRequest.setPassword("newpassword");
        
        UserDetailsImpl userDetails = new UserDetailsImpl(2L, "newuser", "newuser@example.com", "encodedNewPassword", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockedJwtTokenForNewUser");

        AuthResponseDTO response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mockedJwtTokenForNewUser", response.getToken());
        assertEquals(2L, response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("newuser@example.com", response.getEmail());
        assertEquals(Rol.USER, response.getRol());

        verify(usuarioRepository).existsByUsername(registerRequest.getUsername());
        verify(usuarioRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    void testRegister_DuplicateUsername() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("existinguser");
        registerRequest.setEmail("newemail@example.com");
        registerRequest.setPassword("password");

        when(usuarioRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Error: El username ya está en uso!", exception.getMessage());
        verify(usuarioRepository).existsByUsername(registerRequest.getUsername());
        verify(usuarioRepository, never()).existsByEmail(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegister_DuplicateEmail() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("existing@example.com");
        registerRequest.setPassword("password");

        when(usuarioRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Error: El email ya está en uso!", exception.getMessage());
        verify(usuarioRepository).existsByUsername(registerRequest.getUsername());
        verify(usuarioRepository).existsByEmail(registerRequest.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testGetCurrentUser_Success() {
        Usuario currentUser = Usuario.builder()
                .id(1L)
                .nombre("Current User")
                .username("currentuser")
                .email("current@example.com")
                .rol(Rol.USER)
                .edad(30)
                .peso(70.0)
                .altura(175.0)
                .build();
        
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "currentuser", "current@example.com", "encodedPassword", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioRepository.findByUsername(userDetails.getUsername())).thenReturn(Optional.of(currentUser));

        UserInfoDTO userInfo = authService.getCurrentUser();

        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals("currentuser", userInfo.getUsername());
        assertEquals("current@example.com", userInfo.getEmail());
        assertEquals(Rol.USER, userInfo.getRol());
        assertEquals(30, userInfo.getEdad());
        assertEquals(70.0, userInfo.getPeso());
        assertEquals(175.0, userInfo.getAltura());

        verify(usuarioRepository).findByUsername("currentuser");
    }

    @Test
    void testGetCurrentUser_UserNotFoundAfterAuthentication() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "nonexistent", "nonexistent@example.com", "encodedPassword", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioRepository.findByUsername(userDetails.getUsername())).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            authService.getCurrentUser();
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository).findByUsername("nonexistent");
    }

    @Test
    void testLogout_Success() {
        String jwtToken = "mockedJwtToken";
        String jti = "mockedJti";
        Date expirationDate = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

        when(jwtUtils.getJtiFromJwtToken(jwtToken)).thenReturn(jti);
        when(jwtUtils.getExpirationFromJwtToken(jwtToken)).thenReturn(expirationDate);
        when(tokenRevocadoRepository.save(any(TokenRevocado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.logout(jwtToken);

        verify(jwtUtils).getJtiFromJwtToken(jwtToken);
        verify(jwtUtils).getExpirationFromJwtToken(jwtToken);
        verify(tokenRevocadoRepository).save(any(TokenRevocado.class));
    }

    @Test
    void testCleanupExpiredTokens() {
        authService.cleanupExpiredTokens();
        verify(tokenRevocadoRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }
}
