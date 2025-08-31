package com.hairbook.controller;

import com.hairbook.entity.Payment;
import com.hairbook.entity.PaymentStatus;
import com.hairbook.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur pour la gestion des paiements liés aux réservations.
 * Fournit des endpoints pour créer, récupérer et mettre à jour les informations
 * de paiement.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Gestion des paiements")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Construit un PaymentController avec le service requis.
     *
     * @param paymentService Service pour la gestion de la logique des paiements.
     */
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Crée un nouvel enregistrement de paiement.
     *
     * @param payment L'objet de paiement à créer.
     * @return Une entité de réponse avec le paiement créé.
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau paiement")
    public ResponseEntity<Payment> create(@Valid @RequestBody Payment payment) {
        Payment created = paymentService.create(payment);
        return ResponseEntity.created(URI.create("/api/payments/" + created.getId())).body(created);
    }

    /**
     * Récupère un paiement par son ID.
     *
     * @param id L'ID du paiement à récupérer.
     * @return Une entité de réponse avec le paiement, ou non trouvé s'il n'existe
     *         pas.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un paiement par son ID")
    public ResponseEntity<Payment> findById(@PathVariable Long id) {
        return paymentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère le paiement associé à une réservation spécifique.
     *
     * @param reservationId L'ID de la réservation.
     * @return Une entité de réponse avec le paiement, ou non trouvé s'il n'existe
     *         pas.
     */
    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Récupérer le paiement d'une réservation")
    public ResponseEntity<Payment> findByReservation(
            @Parameter(description = "ID de la réservation") @PathVariable Long reservationId) {
        return paymentService.findByReservation(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère tous les paiements qui correspondent à un statut spécifique.
     *
     * @param status Le statut de paiement par lequel filtrer (par exemple, PENDING,
     *               COMPLETED, FAILED).
     * @return Une entité de réponse avec une liste de paiements correspondant au
     *         statut.
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Récupérer les paiements par statut")
    public ResponseEntity<List<Payment>> findByStatus(
            @Parameter(description = "Statut du paiement") @PathVariable PaymentStatus status) {
        List<Payment> payments = paymentService.findByStatus(status);
        return ResponseEntity.ok(payments);
    }

    /**
     * Met à jour le statut d'un paiement spécifique.
     *
     * @param id      L'ID du paiement à mettre à jour.
     * @param status  Le nouveau statut pour le paiement.
     * @param message Un message facultatif à associer à la mise à jour du statut
     *                (par exemple, une raison d'échec).
     * @return Une entité de réponse avec le paiement mis à jour, ou non trouvé s'il
     *         n'existe pas.
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'un paiement")
    public ResponseEntity<Payment> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut") @RequestParam PaymentStatus status,
            @Parameter(description = "Message associé") @RequestParam(required = false) String message) {
        try {
            Payment updated = paymentService.updateStatus(id, status, message);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}