package daw2a.gestionbiblioteca.security.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AuthResponse {

    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String token) {
        this.token = token;
    }

}