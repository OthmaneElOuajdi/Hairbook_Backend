package com.hairbook.controller;

import com.hairbook.entity.BlockedSlot;
import com.hairbook.service.BlockedSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur pour la gestion des créneaux horaires bloqués, permettant aux
 * administrateurs d'empêcher les réservations.
 */
@RestController
@RequestMapping("/api/blocked-slots")
@Tag(name = "Blocked Slots", description = "Gestion des créneaux bloqués")
public class BlockedSlotController {

    private final BlockedSlotService blockedSlotService;

    /**
     * Construit un BlockedSlotController avec le service requis.
     *
     * @param blockedSlotService Service pour la gestion de la logique des créneaux
     *                           bloqués.
     */
    public BlockedSlotController(BlockedSlotService blockedSlotService) {
        this.blockedSlotService = blockedSlotService;
    }

    /**
     * Crée un nouveau créneau horaire bloqué.
     *
     * @param slot L'objet de créneau bloqué à créer.
     * @return Une entité de réponse avec le créneau bloqué créé.
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau créneau bloqué")
    public ResponseEntity<BlockedSlot> create(@Valid @RequestBody BlockedSlot slot) {
        BlockedSlot created = blockedSlotService.create(slot);
        return ResponseEntity.created(URI.create("/api/blocked-slots/" + created.getId())).body(created);
    }

    /**
     * Supprime un créneau bloqué par son ID.
     *
     * @param id L'ID du créneau bloqué à supprimer.
     * @return Une entité de réponse indiquant le succès sans contenu.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un créneau bloqué")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blockedSlotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Trouve tous les créneaux bloqués dans un intervalle de temps donné.
     *
     * @param start La date et l'heure de début de la période.
     * @param end   La date et l'heure de fin de la période.
     * @return Une liste des créneaux bloqués trouvés dans l'intervalle.
     */
    @GetMapping("/between")
    @Operation(summary = "Récupérer les créneaux bloqués dans une période")
    public ResponseEntity<List<BlockedSlot>> findBetween(
            @Parameter(description = "Date/heure de début (format: yyyy-MM-dd'T'HH:mm:ss)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Date/heure de fin (format: yyyy-MM-dd'T'HH:mm:ss)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<BlockedSlot> slots = blockedSlotService.findBetween(start, end);
        return ResponseEntity.ok(slots);
    }
}