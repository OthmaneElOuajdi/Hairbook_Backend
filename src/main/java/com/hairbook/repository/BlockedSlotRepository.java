package com.hairbook.repository;

import com.hairbook.entity.BlockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dépôt pour la gestion des créneaux horaires bloqués.
 * Permet de rechercher des indisponibilités pour des périodes données.
 */
public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long> {
    /**
     * Recherche les créneaux bloqués qui se chevauchent avec une période donnée.
     *
     * @param start L'heure de début de la période à vérifier.
     * @param end L'heure de fin de la période à vérifier.
     * @return Une liste de créneaux bloqués qui chevauchent la période spécifiée.
     */
    List<BlockedSlot> findByStartAtLessThanEqualAndEndAtGreaterThanEqual(
            LocalDateTime start, LocalDateTime end);
}
