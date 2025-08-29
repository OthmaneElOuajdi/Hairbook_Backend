package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.Reservation;
import com.hairbook.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour gérer les opérations de base de données pour l'entité Reservation.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Recherche les réservations par leur statut.
     * @param status Le statut de la réservation à rechercher (ex: PENDING, CONFIRMED).
     * @return Une liste de réservations correspondant au statut.
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * Recherche toutes les réservations pour un utilisateur spécifique.
     * @param userId L'identifiant de l'utilisateur.
     * @return Une liste des réservations de l'utilisateur.
     */
    List<Reservation> findByUser_Id(Long userId);

    /**
     * Recherche les réservations dont l'heure de début se situe dans un intervalle de temps donné.
     * @param start L'heure de début de l'intervalle.
     * @param end L'heure de fin de l'intervalle.
     * @return Une liste de réservations dans la plage horaire spécifiée.
     */
    List<Reservation> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
