package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hairbook.entity.RefreshToken;

import java.util.Optional;

/**
 * Dépôt pour la gestion des tokens de rafraîchissement (refresh tokens) JWT.
 * Ces tokens sont utilisés pour obtenir de nouveaux tokens d'accès sans avoir à se reconnecter.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Recherche un refresh token par sa valeur textuelle.
     *
     * @param token Le token de rafraîchissement.
     * @return Un Optional contenant le RefreshToken s'il est trouvé.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Révoque (supprime) tous les refresh tokens associés à un utilisateur.
     * Utile lors d'une déconnexion globale ou d'un changement de mot de passe.
     *
     * @param userId L'identifiant de l'utilisateur dont les tokens doivent être révoqués.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
    void revokeAllByUserId(@Param("userId") Long userId);
}
