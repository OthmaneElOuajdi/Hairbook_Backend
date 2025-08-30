package com.hairbook.service;

import com.hairbook.dto.auth.AuthResponse;
import com.hairbook.dto.auth.RefreshTokenRequest;
import com.hairbook.entity.RefreshToken;

import java.util.Optional;

/**
 * Service pour la gestion des jetons de rafraîchissement (refresh tokens).
 * Permet de créer, trouver, révoquer et utiliser des jetons pour renouveler les
 * sessions d'authentification.
 */
public interface RefreshTokenService {
    /**
     * Crée un nouveau jeton de rafraîchissement pour un utilisateur donné.
     *
     * @param userId L'ID de l'utilisateur pour lequel créer le jeton.
     * @return Le jeton de rafraîchissement créé.
     */
    RefreshToken create(Long userId);

    /**
     * Recherche un jeton de rafraîchissement par sa valeur.
     *
     * @param token La chaîne de caractères du jeton.
     * @return Un Optional contenant le jeton s'il est trouvé et valide.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Révoque tous les jetons de rafraîchissement actifs pour un utilisateur donné.
     * Typiquement utilisé lors de la déconnexion.
     *
     * @param userId L'ID de l'utilisateur dont les jetons doivent être révoqués.
     */
    void revokeByUserId(Long userId);

    // Pour /api/auth/refresh-token
    /**
     * Rafraîchit un jeton d'authentification en utilisant un jeton de
     * rafraîchissement valide.
     *
     * @param request La demande contenant le jeton de rafraîchissement.
     * @return Une nouvelle réponse d'authentification avec un nouveau jeton d'accès
     *         (JWT).
     */
    AuthResponse refresh(RefreshTokenRequest request);
}
