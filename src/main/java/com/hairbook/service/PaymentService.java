package com.hairbook.service;

import java.util.List;
import java.util.Optional;

import com.hairbook.entity.Payment;
import com.hairbook.entity.PaymentStatus;

/**
 * Service pour la gestion des paiements.
 * Gère la création, la recherche et la mise à jour des paiements liés aux
 * réservations.
 */
public interface PaymentService {
    /**
     * Crée un nouveau paiement.
     *
     * @param p Le paiement à créer.
     * @return Le paiement créé.
     */
    Payment create(Payment p);

    /**
     * Trouve un paiement par son ID.
     *
     * @param id L'ID du paiement.
     * @return Un Optional contenant le paiement s'il est trouvé.
     */
    Optional<Payment> findById(Long id);

    /**
     * Trouve un paiement associé à une réservation.
     *
     * @param reservationId L'ID de la réservation.
     * @return Un Optional contenant le paiement s'il est trouvé.
     */
    Optional<Payment> findByReservation(Long reservationId);

    /**
     * Met à jour le statut d'un paiement.
     *
     * @param paymentId L'ID du paiement à mettre à jour.
     * @param status    Le nouveau statut du paiement.
     * @param message   Un message optionnel (ex: raison de l'échec).
     * @return Le paiement mis à jour.
     */
    Payment updateStatus(Long paymentId, PaymentStatus status, String message);

    /**
     * Trouve tous les paiements avec un statut spécifique.
     *
     * @param status Le statut des paiements à rechercher.
     * @return Une liste des paiements correspondants.
     */
    List<Payment> findByStatus(PaymentStatus status);
}
