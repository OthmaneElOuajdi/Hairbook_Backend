package com.hairbook.controller;

import com.hairbook.dto.auth.AuthRequest;
import com.hairbook.dto.auth.AuthResponse;
import com.hairbook.dto.auth.RefreshTokenRequest;
import com.hairbook.entity.User;
import com.hairbook.service.UserService;
import com.hairbook.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des processus d'authentification des utilisateurs.
 * Fournit des endpoints pour la connexion, l'inscription, le rafraîchissement
 * de token et la déconnexion.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints d'authentification et gestion des tokens")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Construit un AuthController avec les services nécessaires.
     *
     * @param userService         Service pour les opérations liées à l'utilisateur
     *                            comme l'authentification et l'inscription.
     * @param refreshTokenService Service pour la gestion des refresh tokens.
     */
    public AuthController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Authentifie un utilisateur et retourne un JWT et un refresh token.
     *
     * @param request La requête d'authentification contenant les identifiants.
     * @return Une entité de réponse avec les tokens d'authentification.
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion d'un utilisateur")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Enregistre un nouvel utilisateur dans le système.
     *
     * @param user L'objet utilisateur à enregistrer.
     * @return Une entité de réponse avec les détails de l'utilisateur créé.
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<User> register(@RequestBody User user) {
        User created = userService.register(user);
        return ResponseEntity.ok(created);
    }

    /**
     * Rafraîchit un JWT expiré en utilisant un refresh token valide.
     *
     * @param request La requête contenant le refresh token.
     * @return Une entité de réponse avec un nouvel ensemble de tokens
     *         d'authentification.
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Rafraîchir un token JWT via refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenService.refresh(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Déconnecte un utilisateur en révoquant ses refresh tokens.
     *
     * @param userId L'ID de l'utilisateur à déconnecter.
     * @return Une entité de réponse indiquant le succès sans contenu.
     */
    @PostMapping("/logout/{userId}")
    @Operation(summary = "Déconnexion et révocation des tokens")
    public ResponseEntity<Void> logout(@PathVariable Long userId) {
        refreshTokenService.revokeByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère les détails de l'utilisateur actuellement authentifié à partir du
     * JWT.
     *
     * @param authHeader L'en-tête Authorization contenant le token Bearer.
     * @return Une entité de réponse avec les détails de l'utilisateur actuel.
     */
    @GetMapping("/me")
    @Operation(summary = "Récupérer les informations de l'utilisateur connecté")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        // Extraire le token du header Authorization
        String token = authHeader.replace("Bearer ", "");
        User currentUser = userService.getCurrentUserFromToken(token);
        return ResponseEntity.ok(currentUser);
    }
}
