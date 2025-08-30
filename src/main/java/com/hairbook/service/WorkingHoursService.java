package com.hairbook.service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import com.hairbook.entity.WorkingHours;

/**
 * Service pour la gestion des horaires de travail du salon.
 * Permet de définir et de consulter les heures d'ouverture pour chaque jour de
 * la semaine.
 */
public interface WorkingHoursService {
    /**
     * Crée ou met à jour les horaires de travail pour un jour donné.
     *
     * @param wh L'objet WorkingHours contenant le jour et les heures.
     * @return Les horaires de travail sauvegardés.
     */
    WorkingHours save(WorkingHours wh);

    /**
     * Trouve les horaires de travail pour un jour spécifique de la semaine.
     *
     * @param day Le jour de la semaine (DayOfWeek).
     * @return Un Optional contenant les horaires de travail pour ce jour.
     */
    Optional<WorkingHours> findByDay(DayOfWeek day);

    /**
     * Récupère tous les horaires de travail définis pour la semaine.
     *
     * @return Une liste de tous les horaires de travail.
     */
    List<WorkingHours> findAll();
}
