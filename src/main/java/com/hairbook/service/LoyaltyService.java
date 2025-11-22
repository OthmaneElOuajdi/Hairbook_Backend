package com.hairbook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service de gestion des points de fidélité et des récompenses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final UserRepository userRepository;

    /** Points nécessaires pour obtenir un service gratuit */
    public static final int POINTS_FOR_FREE_SERVICE = 1000;

    /**
     * Récupère l'état des points de fidélité d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @return informations sur les points et récompenses
     */
    public LoyaltyRewardDto getLoyaltyStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Integer currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        Integer availableFreeServices = currentPoints / POINTS_FOR_FREE_SERVICE;
        Integer remainingPoints = currentPoints % POINTS_FOR_FREE_SERVICE;
        Integer progressPercentage = (remainingPoints * 100) / POINTS_FOR_FREE_SERVICE;

        String message;
        if (availableFreeServices > 0) {
            message = String.format("Félicitations ! Vous avez %d service(s) gratuit(s) disponible(s)", availableFreeServices);
        } else {
            int pointsNeeded = POINTS_FOR_FREE_SERVICE - remainingPoints;
            message = String.format("Plus que %d points pour un service gratuit !", pointsNeeded);
        }

        return LoyaltyRewardDto.builder()
                .currentPoints(currentPoints)
                .pointsNeeded(POINTS_FOR_FREE_SERVICE)
                .progressPercentage(progressPercentage)
                .availableFreeServices(availableFreeServices)
                .message(message)
                .build();
    }

    /**
     * Vérifie si un utilisateur a suffisamment de points pour un service gratuit.
     *
     * @param userId identifiant de l'utilisateur
     * @return true si l'utilisateur peut échanger des points
     */
    public boolean canRedeemPoints(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Integer currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        return currentPoints >= POINTS_FOR_FREE_SERVICE;
    }

    /**
     * Déduit des points du compte d'un utilisateur pour un service gratuit.
     *
     * @param userId identifiant de l'utilisateur
     * @param pointsToRedeem nombre de points à déduire
     * @return utilisateur mis à jour
     */
    @Transactional
    public User redeemPoints(UUID userId, Integer pointsToRedeem) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Integer currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;

        if (currentPoints < pointsToRedeem) {
            throw new IllegalArgumentException("Points insuffisants");
        }

        if (pointsToRedeem != POINTS_FOR_FREE_SERVICE) {
            throw new IllegalArgumentException("Vous devez échanger exactement " + POINTS_FOR_FREE_SERVICE + " points");
        }

        user.setLoyaltyPoints(currentPoints - pointsToRedeem);
        User updatedUser = userRepository.save(user);

        log.info("User {} redeemed {} points. New balance: {}",
                user.getEmail(), pointsToRedeem, updatedUser.getLoyaltyPoints());

        return updatedUser;
    }

    /**
     * Ajoute des points au compte d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @param points nombre de points à ajouter
     * @return utilisateur mis à jour
     */
    @Transactional
    public User addPoints(UUID userId, Integer points) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Integer currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        user.setLoyaltyPoints(currentPoints + points);

        User updatedUser = userRepository.save(user);
        log.info("Added {} points to user {}. New balance: {}",
                points, user.getEmail(), updatedUser.getLoyaltyPoints());

        return updatedUser;
    }
}
