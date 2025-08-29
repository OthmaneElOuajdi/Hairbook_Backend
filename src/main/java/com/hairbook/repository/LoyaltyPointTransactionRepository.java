package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.LoyaltyPointTransaction;

import java.util.List;

/**
 * Dépôt pour la gestion des transactions de points de fidélité.
 */
@Repository
public interface LoyaltyPointTransactionRepository extends JpaRepository<LoyaltyPointTransaction, Long> {
    /**
     * Recherche toutes les transactions de points de fidélité pour un utilisateur donné,
     * triées par date de transaction décroissante.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return Une liste de transactions de points de fidélité.
     */
    List<LoyaltyPointTransaction> findByUser_IdOrderByTransactionDateDesc(Long userId);
}
