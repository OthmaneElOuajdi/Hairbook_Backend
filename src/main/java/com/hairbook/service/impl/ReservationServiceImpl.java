package com.hairbook.service.impl;

import com.hairbook.entity.Reservation;
import com.hairbook.entity.ReservationStatus;
import com.hairbook.repository.ReservationRepository;
import com.hairbook.service.ReservationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service pour la gestion des réservations.
 * Fournit la logique métier pour créer, rechercher, mettre à jour et supprimer
 * des réservations.
 */
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * Constructeur pour l'injection du repository des réservations.
     *
     * @param reservationRepository Le repository pour les entités Reservation.
     */
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Reservation create(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> findByUser(Long userId) {
        return reservationRepository.findByUser_Id(userId);
    }

    @Override
    public List<Reservation> findBetween(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findByStartTimeBetween(start, end);
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    /**
     * Met à jour le statut d'une réservation existante.
     *
     * @param id     L'ID de la réservation à mettre à jour.
     * @param status Le nouveau statut de la réservation.
     * @return La réservation mise à jour.
     * @throws IllegalArgumentException si la réservation n'est pas trouvée.
     */
    @Override
    public Reservation updateStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée"));
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }
}
