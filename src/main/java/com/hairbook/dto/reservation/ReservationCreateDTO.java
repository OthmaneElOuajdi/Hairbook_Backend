package com.hairbook.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Payload pour créer une réservation")
public class ReservationCreateDTO {

    @Schema(description = "Identifiant du service (coupe, coloration...)")
    @NotNull
    private Long serviceItemId;

    @Schema(description = "Date/heure souhaitée du rendez-vous (ISO-8601)")
    @NotNull
    @Future
    private LocalDateTime reservationDateTime;

    @Schema(description = "Note optionnelle pour le coiffeur")
    @Size(max = 500)
    private String notes;

    public ReservationCreateDTO() {
    }

    public ReservationCreateDTO(Long serviceItemId, LocalDateTime reservationDateTime, String notes) {
        this.serviceItemId = serviceItemId;
        this.reservationDateTime = reservationDateTime;
        this.notes = notes;
    }

    public Long getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(Long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
