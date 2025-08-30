package com.hairbook.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hairbook.entity.Reservation;
import com.hairbook.entity.ReservationStatus;

/**
 * Service pour la gestion des réservations.
 * Fournit la logique métier pour créer, rechercher, mettre à jour et supprimer
 * des réservations.
 */
public interface ReservationService {
    /**
     * Crée une nouvelle réservation.
     *
     * @param r La réservation à créer.
     * @return La réservation créée.
     */
    Reservation create(Reservation r);

    /**
     * Trouve une réservation par son ID.
     *
     * @param id L'ID de la réservation.
     * @return Un Optional contenant la réservation si elle est trouvée.
     */
    Optional<Reservation> findById(Long id);

    /**
     * Trouve toutes les réservations pour un utilisateur donné.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une liste des réservations de l'utilisateur.
     */
    List<Reservation> findByUser(Long userId);

    /**
     * Trouve toutes les réservations dans un intervalle de dates donné.
     *
     * @param start La date de début de l'intervalle.
     * @param end   La date de fin de l'intervalle.
     * @return Une liste des réservations dans l'intervalle.
     */
    List<Reservation> findBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Trouve toutes les réservations avec un statut spécifique.
     *
     * @param status Le statut de la réservation à rechercher.
     * @return Une liste des réservations correspondantes.
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * Met à jour le statut d'une réservation.
     *
     * @param reservationId L'ID de la réservation à mettre à jour.
     * @param status        Le nouveau statut de la réservation.
     * @return La réservation mise à jour.
     */
    Reservation updateStatus(Long reservationId, ReservationStatus status);

    /**
     * Supprime une réservation par son ID.
     *
     * @param id L'ID de la réservation à supprimer.
     */
    void deleteById(Long id);
}
