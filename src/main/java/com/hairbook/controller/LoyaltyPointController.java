package com.hairbook.controller;

import com.hairbook.entity.LoyaltyPointTransaction;
import com.hairbook.service.LoyaltyPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur pour la gestion des points de fidélité des utilisateurs.
 * Fournit des endpoints pour attribuer, utiliser et consulter le solde et
 * l'historique des points de fidélité.
 */
@RestController
@RequestMapping("/api/loyalty-points")
@Tag(name = "Loyalty Points", description = "Gestion des points de fidélité")
public class LoyaltyPointController {

    private final LoyaltyPointService loyaltyPointService;

    /**
     * Construit un LoyaltyPointController avec le service requis.
     *
     * @param loyaltyPointService Service pour la gestion de la logique des points
     *                            de fidélité.
     */
    public LoyaltyPointController(LoyaltyPointService loyaltyPointService) {
        this.loyaltyPointService = loyaltyPointService;
    }

    /**
     * Attribue des points de fidélité à un utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur à qui attribuer les points.
     * @param points Le nombre de points à attribuer.
     * @param reason Une description de la raison pour laquelle les points sont
     *               attribués.
     * @return Une entité de réponse avec la transaction de points de fidélité
     *         créée.
     */
    @PostMapping("/award")
    @Operation(summary = "Attribuer des points de fidélité à un utilisateur")
    public ResponseEntity<LoyaltyPointTransaction> awardPoints(
            @Parameter(description = "ID de l'utilisateur") @RequestParam Long userId,
            @Parameter(description = "Nombre de points à attribuer") @RequestParam int points,
            @Parameter(description = "Raison de l'attribution") @RequestParam String reason) {

        LoyaltyPointTransaction transaction = loyaltyPointService.awardPoints(userId, points, reason);
        return ResponseEntity.created(URI.create("/api/loyalty-points/transactions/" + transaction.getId()))
                .body(transaction);
    }

    /**
     * Utilise les points de fidélité pour un utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur qui utilise les points.
     * @param points Le nombre de points à utiliser.
     * @param reason Une description de la raison pour laquelle les points sont
     *               utilisés.
     * @return Une entité de réponse avec la transaction de points de fidélité
     *         créée, ou une mauvaise requête si les points sont insuffisants.
     */
    @PostMapping("/redeem")
    @Operation(summary = "Utiliser des points de fidélité")
    public ResponseEntity<LoyaltyPointTransaction> redeemPoints(
            @Parameter(description = "ID de l'utilisateur") @RequestParam Long userId,
            @Parameter(description = "Nombre de points à utiliser") @RequestParam int points,
            @Parameter(description = "Raison de l'utilisation") @RequestParam String reason) {

        try {
            LoyaltyPointTransaction transaction = loyaltyPointService.redeemPoints(userId, points, reason);
            return ResponseEntity.created(URI.create("/api/loyalty-points/transactions/" + transaction.getId()))
                    .body(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère le solde total de points de fidélité pour un utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une entité de réponse avec le total des points de l'utilisateur.
     */
    @GetMapping("/user/{userId}/total")
    @Operation(summary = "Récupérer le total des points d'un utilisateur")
    public ResponseEntity<Integer> getTotalPoints(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId) {
        int totalPoints = loyaltyPointService.getTotalPoints(userId);
        return ResponseEntity.ok(totalPoints);
    }

    /**
     * Récupère l'historique des transactions de points de fidélité pour un
     * utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une entité de réponse avec la liste des transactions de points de
     *         fidélité de l'utilisateur.
     */
    @GetMapping("/user/{userId}/history")
    @Operation(summary = "Récupérer l'historique des transactions de points")
    public ResponseEntity<List<LoyaltyPointTransaction>> getHistory(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId) {
        List<LoyaltyPointTransaction> history = loyaltyPointService.history(userId);
        return ResponseEntity.ok(history);
    }
}