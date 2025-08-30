package com.hairbook.dto.auth;

import com.hairbook.validation.ValidNonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requête d'authentification (login)")
public class AuthRequest {

    @Schema(description = "Adresse e-mail de l'utilisateur (emails jetables interdits)")
    @NotBlank(message = "L'adresse e-mail est obligatoire")
    @Email(message = "Format d'adresse e-mail invalide")
    @ValidNonDisposableEmail
    private String email;

    @Schema(description = "Mot de passe en texte clair (transmis via HTTPS)")
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    public AuthRequest() {
    }

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
