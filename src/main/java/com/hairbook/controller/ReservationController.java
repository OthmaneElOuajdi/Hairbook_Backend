package com.hairbook.controller;

import com.hairbook.dto.reservation.ReservationCreateDTO;
import com.hairbook.dto.reservation.ReservationResponseDTO;
import com.hairbook.entity.Reservation;
import com.hairbook.entity.ReservationStatus;
import com.hairbook.entity.User;
import com.hairbook.mapper.ReservationMapper;
import com.hairbook.service.ReservationService;
import com.hairbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des réservations des clients.
 * Fournit des endpoints pour créer, récupérer, mettre à jour et supprimer des
 * réservations.
 */
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Endpoints de gestion des réservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final ReservationMapper reservationMapper;

    /**
     * Construit un ReservationController avec les services requis.
     *
     * @param reservationService Service pour la gestion de la logique des
     *                           réservations.
     * @param userService        Service pour la gestion des utilisateurs.
     */
    public ReservationController(ReservationService reservationService, UserService userService,
            ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.reservationMapper = reservationMapper;
    }

    /**
     * Crée une nouvelle réservation pour l'utilisateur connecté.
     *
     * @param dto            L'objet de transfert de données pour la création de la
     *                       réservation.
     * @param authentication L'objet d'authentification contenant les détails de
     *                       l'utilisateur connecté.
     * @return Une entité de réponse avec la réservation créée.
     */
    @PostMapping
    @Operation(summary = "Créer une réservation")
    public ResponseEntity<Reservation> create(
            @Valid @RequestBody ReservationCreateDTO dto,
            Authentication authentication) {
        Reservation r = new Reservation();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        r.setUser(user);

        ServiceItem s = new ServiceItem();
        s.setId(dto.getServiceItemId());
        r.setServiceItem(s);

        r.setStartTime(dto.getReservationDateTime());
        r.setStatus(ReservationStatus.PENDING);

        Reservation created = reservationService.create(r);
        return ResponseEntity.created(URI.create("/api/reservations/" + created.getId())).body(created);
    }

    /**
     * Récupère une réservation par son ID.
     *
     * @param id L'ID de la réservation à récupérer.
     * @return Une entité de réponse avec la réservation, ou non trouvée si elle
     *         n'existe pas.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une réservation par id")
    public ResponseEntity<Reservation> findById(@PathVariable Long id) {
        Optional<Reservation> opt = reservationService.findById(id);
        return opt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère toutes les réservations pour un utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une entité de réponse avec une liste des réservations de
     *         l'utilisateur.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les réservations d’un utilisateur")
    public ResponseEntity<List<Reservation>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findByUser(userId));
    }

    /**
     * Récupère toutes les réservations de l'utilisateur connecté.
     *
     * @param authentication L'objet d'authentification contenant les détails de
     *                       l'utilisateur connecté.
     * @return Une entité de réponse avec une liste des réservations de
     *         l'utilisateur connecté.
     */
    @GetMapping("/my")
    @Operation(summary = "Lister mes réservations")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        return userService.findByEmail(email)
                .map(user -> {
                    List<Reservation> reservations = reservationService.findByUser(user.getId());
                    List<ReservationResponseDTO> dtos = reservationMapper.toResponseDTOList(reservations);
                    return ResponseEntity.ok(dtos);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Trouve toutes les réservations dans un intervalle de temps donné.
     *
     * @param start La date et l'heure de début de la période.
     * @param end   La date et l'heure de fin de la période.
     * @return Une liste des réservations trouvées dans l'intervalle.
     */
    @GetMapping("/between")
    @Operation(summary = "Lister les réservations dans un intervalle de temps")
    public ResponseEntity<List<Reservation>> findBetween(
            @RequestParam @Parameter(description = "Date/heure début (ISO 8601)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @Parameter(description = "Date/heure fin (ISO 8601)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(reservationService.findBetween(start, end));
    }

    /**
     * Met à jour le statut d'une réservation (par exemple, de PENDING à CONFIRMED).
     *
     * @param id     L'ID de la réservation à mettre à jour.
     * @param status Le nouveau statut de la réservation.
     * @return Une entité de réponse avec la réservation mise à jour.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d’une réservation")
    public ResponseEntity<Reservation> updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        Reservation updated = reservationService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    /**
     * Annule une réservation en changeant son statut à CANCELLED.
     *
     * @param id L'ID de la réservation à annuler.
     * @return Une entité de réponse avec la réservation mise à jour.
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler une réservation")
    public ResponseEntity<ReservationResponseDTO> cancelReservation(@PathVariable Long id) {
        Reservation cancelled = reservationService.updateStatus(id, ReservationStatus.CANCELLED);
        ReservationResponseDTO dto = reservationMapper.toResponseDTO(cancelled);
        return ResponseEntity.ok(dto);
    }

    /**
     * Supprime une réservation par son ID.
     *
     * @param id L'ID de la réservation à supprimer.
     * @return Une entité de réponse indiquant le succès sans contenu.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
