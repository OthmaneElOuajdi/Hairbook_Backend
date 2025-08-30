package com.hairbook.service;

import java.util.List;

import com.hairbook.entity.LoyaltyPointTransaction;

/**
 * Service pour la gestion des points de fidélité des utilisateurs.
 * Permet d'attribuer, d'utiliser et de consulter les points de fidélité.
 */
public interface LoyaltyPointService {
    /**
     * Attribue des points de fidélité à un utilisateur.
     *
     * @param userId L'ID de l'utilisateur qui reçoit les points.
     * @param points Le nombre de points à attribuer.
     * @param reason La raison de l'attribution (ex: "Achat de produit").
     * @return La transaction de points de fidélité créée.
     */
    LoyaltyPointTransaction awardPoints(Long userId, int points, String reason);

    /**
     * Utilise les points de fidélité d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur qui utilise les points.
     * @param points Le nombre de points à utiliser (doit être négatif).
     * @param reason La raison de l'utilisation (ex: "Réduction sur service").
     * @return La transaction de points de fidélité créée.
     */
    LoyaltyPointTransaction redeemPoints(Long userId, int points, String reason);

    /**
     * Calcule le solde total de points de fidélité pour un utilisateur.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Le solde total de points.
     */
    int getTotalPoints(Long userId);

    /**
     * Récupère l'historique des transactions de points de fidélité pour un
     * utilisateur.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une liste des transactions de points.
     */
    List<LoyaltyPointTransaction> history(Long userId);
}
