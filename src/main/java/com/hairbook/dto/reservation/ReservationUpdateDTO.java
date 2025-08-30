package com.hairbook.dto.reservation;

import com.hairbook.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Données pour mettre à jour une réservation")
public class ReservationUpdateDTO {

    @Schema(description = "Nouvelle date et heure de la réservation")
    @Future(message = "La date de réservation doit être dans le futur")
    private LocalDateTime reservationDateTime;

    @Schema(description = "Nouveau statut de la réservation")
    private ReservationStatus status;

    @Schema(description = "Nouvelles notes")
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;

    public ReservationUpdateDTO() {
    }

    public ReservationUpdateDTO(LocalDateTime reservationDateTime, ReservationStatus status, String notes) {
        this.reservationDateTime = reservationDateTime;
        this.status = status;
        this.notes = notes;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
