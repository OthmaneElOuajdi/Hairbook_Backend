package com.hairbook.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Détails d'une réservation")
public class ReservationDTO {

    @Schema(description = "Identifiant de la réservation")
    private Long id;

    @Schema(description = "Identifiant de l'utilisateur")
    private Long userId;

    @Schema(description = "Identifiant du service")
    private Long serviceItemId;

    @Schema(description = "Nom du service")
    private String serviceName;

    @Schema(description = "Prix du service")
    private BigDecimal price;

    @Schema(description = "Durée du service en minutes")
    private Integer durationMinutes;

    @Schema(description = "Date/heure du rendez-vous")
    private LocalDateTime reservationDateTime;

    @Schema(description = "Statut de la réservation")
    private String status;

    @Schema(description = "Note éventuelle")
    private String notes;

    public ReservationDTO() {
    }

    public ReservationDTO(Long id, Long userId, Long serviceItemId, String serviceName, BigDecimal price,
            Integer durationMinutes, LocalDateTime reservationDateTime, String status, String notes) {
        this.id = id;
        this.userId = userId;
        this.serviceItemId = serviceItemId;
        this.serviceName = serviceName;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.reservationDateTime = reservationDateTime;
        this.status = status;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(Long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
