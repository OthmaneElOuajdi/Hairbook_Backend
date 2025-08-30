package com.hairbook.service.impl;

import com.hairbook.entity.LoyaltyPointTransaction;
import com.hairbook.entity.User;
import com.hairbook.repository.LoyaltyPointTransactionRepository;
import com.hairbook.repository.UserRepository;
import com.hairbook.service.LoyaltyPointService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation du service de gestion des points de fidélité.
 * Gère l'attribution, l'utilisation et la consultation des points pour les
 * utilisateurs.
 */
@Service
public class LoyaltyPointServiceImpl implements LoyaltyPointService {

    private final LoyaltyPointTransactionRepository loyaltyPointTransactionRepository;
    private final UserRepository userRepository;

    /**
     * Constructeur pour l'injection des repositories nécessaires.
     *
     * @param loyaltyPointTransactionRepository Repository pour les transactions de
     *                                          points de fidélité.
     * @param userRepository                    Repository pour les utilisateurs.
     */
    public LoyaltyPointServiceImpl(LoyaltyPointTransactionRepository loyaltyPointTransactionRepository,
            UserRepository userRepository) {
        this.loyaltyPointTransactionRepository = loyaltyPointTransactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LoyaltyPointTransaction awardPoints(Long userId, int points, String reason) {
        return createTransaction(userId, points, reason);
    }

    @Override
    public LoyaltyPointTransaction redeemPoints(Long userId, int points, String reason) {
        return createTransaction(userId, -Math.abs(points), reason); // on soustrait les points
    }

    @Override
    public int getTotalPoints(Long userId) {
        return loyaltyPointTransactionRepository.findByUser_IdOrderByTransactionDateDesc(userId)
                .stream()
                .mapToInt(LoyaltyPointTransaction::getPoints)
                .sum();
    }

    @Override
    public List<LoyaltyPointTransaction> history(Long userId) {
        return loyaltyPointTransactionRepository.findByUser_IdOrderByTransactionDateDesc(userId);
    }

    /**
     * Méthode privée pour créer et sauvegarder une transaction de points de
     * fidélité.
     * Gère à la fois l'ajout (points positifs) et le retrait (points négatifs).
     *
     * @param userId      L'ID de l'utilisateur concerné.
     * @param points      Le nombre de points à ajouter ou retirer.
     * @param description La raison de la transaction.
     * @return La transaction de points de fidélité créée.
     * @throws RuntimeException si l'utilisateur n'est pas trouvé.
     */
    private LoyaltyPointTransaction createTransaction(Long userId, int points, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        LoyaltyPointTransaction transaction = new LoyaltyPointTransaction();
        transaction.setUser(user);
        transaction.setPoints(points);
        transaction.setDescription(description);

        return loyaltyPointTransactionRepository.save(transaction);
    }
}
