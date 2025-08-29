package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.WorkingHours;

import java.time.DayOfWeek;
import java.util.Optional;

/**
 * Dépôt pour la gestion des horaires de travail.
 * Permet de définir et de récupérer les heures d'ouverture pour chaque jour de la semaine.
 */
@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    /**
     * Recherche les horaires de travail pour un jour de la semaine spécifique.
     *
     * @param dayOfWeek Le jour de la semaine (lundi, mardi, etc.).
     * @return Un Optional contenant les horaires de travail pour ce jour, s'ils sont définis.
     */
    Optional<WorkingHours> findByDayOfWeek(DayOfWeek dayOfWeek);
}
