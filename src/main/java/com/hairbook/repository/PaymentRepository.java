package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.Payment;
import com.hairbook.entity.PaymentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Dépôt pour la gestion des paiements liés aux réservations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Recherche un paiement par l'identifiant de sa réservation associée.
     * Note : Cette méthode et findByReservation_Id semblent redondantes.
     *
     * @param reservationId L'identifiant de la réservation.
     * @return Un Optional contenant le paiement s'il est trouvé.
     */
    Optional<Payment> findByReservationId(Long reservationId);

    /**
     * Recherche un paiement en utilisant la navigation via l'objet Reservation.
     * Note : Préférer cette syntaxe car elle est plus explicite sur la relation.
     *
     * @param reservationId L'identifiant de la réservation.
     * @return Un Optional contenant le paiement s'il est trouvé.
     */
    Optional<Payment> findByReservation_Id(Long reservationId);

    /**
     * Recherche les paiements par leur statut (par exemple, EN_ATTENTE, RÉUSSI, ÉCHOUÉ).
     *
     * @param status Le statut du paiement à rechercher.
     * @return Une liste de paiements correspondant au statut.
     */
    List<Payment> findByStatus(PaymentStatus status);
}
