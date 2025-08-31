package com.hairbook.controller;

import com.hairbook.entity.WorkingHours;
import com.hairbook.service.WorkingHoursService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.DayOfWeek;
import java.util.List;

/**
 * Contrôleur pour la gestion des heures d'ouverture du salon.
 * Fournit des endpoints pour créer, mettre à jour et récupérer les heures
 * d'ouverture pour chaque jour de la semaine.
 */
@RestController
@RequestMapping("/api/working-hours")
@Tag(name = "Working Hours", description = "Gestion des heures d'ouverture")
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    /**
     * Construit un WorkingHoursController avec le service requis.
     *
     * @param workingHoursService Service pour la gestion de la logique des heures
     *                            d'ouverture.
     */
    public WorkingHoursController(WorkingHoursService workingHoursService) {
        this.workingHoursService = workingHoursService;
    }

    /**
     * Crée ou met à jour les heures d'ouverture pour un jour spécifique.
     * Si des heures d'ouverture pour le jour donné existent déjà, elles seront
     * mises à jour. Sinon, elles seront créées.
     *
     * @param workingHours L'objet des heures d'ouverture à enregistrer.
     * @return Une entité de réponse avec les heures d'ouverture enregistrées.
     */
    @PostMapping
    @Operation(summary = "Créer ou mettre à jour les heures d'ouverture")
    public ResponseEntity<WorkingHours> save(@Valid @RequestBody WorkingHours workingHours) {
        WorkingHours saved = workingHoursService.save(workingHours);
        return ResponseEntity.created(URI.create("/api/working-hours/" + saved.getId())).body(saved);
    }

    /**
     * Récupère toutes les heures d'ouverture définies pour la semaine.
     *
     * @return Une entité de réponse avec une liste de toutes les heures
     *         d'ouverture.
     */
    @GetMapping
    @Operation(summary = "Récupérer toutes les heures d'ouverture")
    public ResponseEntity<List<WorkingHours>> findAll() {
        List<WorkingHours> workingHours = workingHoursService.findAll();
        return ResponseEntity.ok(workingHours);
    }

    /**
     * Récupère les heures d'ouverture pour un jour spécifique de la semaine.
     *
     * @param day Le jour de la semaine (par exemple, MONDAY, TUESDAY).
     * @return Une entité de réponse avec les heures d'ouverture pour ce jour, ou
     *         non trouvées si elles ne sont pas définies.
     */
    @GetMapping("/day/{day}")
    @Operation(summary = "Récupérer les heures d'ouverture d'un jour spécifique")
    public ResponseEntity<WorkingHours> findByDay(
            @Parameter(description = "Jour de la semaine (MONDAY, TUESDAY, etc.)") @PathVariable DayOfWeek day) {
        return workingHoursService.findByDay(day)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}