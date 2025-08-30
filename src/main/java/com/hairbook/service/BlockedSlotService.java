package com.hairbook.service;

import java.time.LocalDateTime;
import java.util.List;

import com.hairbook.entity.BlockedSlot;

/**
 * Service pour la gestion des créneaux horaires bloqués.
 * Permet aux administrateurs ou aux coiffeurs de définir des périodes
 * d'indisponibilité.
 */
public interface BlockedSlotService {
    /**
     * Crée un nouveau créneau bloqué.
     *
     * @param slot Le créneau à bloquer (contenant les dates de début et de fin).
     * @return Le créneau bloqué qui a été créé.
     */
    BlockedSlot create(BlockedSlot slot);

    /**
     * Supprime un créneau bloqué par son ID.
     *
     * @param id L'ID du créneau bloqué à supprimer.
     */
    void deleteById(Long id);

    /**
     * Recherche tous les créneaux bloqués qui se chevauchent avec un intervalle de
     * temps donné.
     *
     * @param start La date de début de l'intervalle de recherche.
     * @param end   La date de fin de l'intervalle de recherche.
     * @return Une liste de créneaux bloqués trouvés dans cet intervalle.
     */
    List<BlockedSlot> findBetween(LocalDateTime start, LocalDateTime end);
}
