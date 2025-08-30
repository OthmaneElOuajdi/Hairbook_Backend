package com.hairbook.service.impl;

import com.hairbook.dto.auth.AuthResponse;
import com.hairbook.dto.auth.RefreshTokenRequest;
import com.hairbook.entity.RefreshToken;
import com.hairbook.entity.User;
import com.hairbook.repository.RefreshTokenRepository;
import com.hairbook.repository.UserRepository;
import com.hairbook.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation du service de gestion des jetons de rafraîchissement (refresh
 * tokens).
 * Gère la création, la validation, la révocation et le renouvellement des
 * jetons.
 */
@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepository;

    /**
     * Constructeur pour l'injection des repositories nécessaires.
     *
     * @param repo           Repository pour les entités RefreshToken.
     * @param userRepository Repository pour les entités User.
     */
    public RefreshTokenServiceImpl(RefreshTokenRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshToken create(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        RefreshToken t = new RefreshToken();
        t.setUser(user); // on passe l'objet User
        t.setToken(UUID.randomUUID().toString());
        t.setExpiresAt(LocalDateTime.now().plusDays(7));
        t.setRevoked(false);
        return repo.save(t);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return repo.findByToken(token);
    }

    public void revokeByUserId(Long userId) {
        repo.revokeAllByUserId(userId);
    }

    /**
     * Rafraîchit un jeton d'accès en utilisant un jeton de rafraîchissement.
     * Implémente une stratégie de rotation de jeton : l'ancien jeton de
     * rafraîchissement est révoqué
     * et un nouveau est émis avec le nouveau jeton d'accès.
     * NOTE : La génération du jeton d'accès (JWT) est actuellement simulée
     * (`mock`).
     *
     * @param request La demande contenant le jeton de rafraîchissement à utiliser.
     * @return Une nouvelle réponse d'authentification avec un nouveau jeton d'accès
     *         et un nouveau jeton de rafraîchissement.
     * @throws IllegalArgumentException si le jeton de rafraîchissement est
     *                                  invalide, expiré ou révoqué.
     */
    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken existing = repo.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh token invalide"));

        if (existing.isRevoked() || existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expiré ou révoqué");
        }

        String newAccessToken = "mock-jwt-for-user-" + existing.getUser().getId();
        long expiresInSeconds = 3600L;

        existing.setRevoked(true);
        repo.save(existing);

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(existing.getUser());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        newToken.setRevoked(false);
        repo.save(newToken);

        return new AuthResponse(newAccessToken, newToken.getToken(), expiresInSeconds);
    }
}
