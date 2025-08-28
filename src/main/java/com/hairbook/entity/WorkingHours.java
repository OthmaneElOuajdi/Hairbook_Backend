package com.hairbook.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "working_hours", uniqueConstraints = {
        @UniqueConstraint(name = "uk_workinghours_day", columnNames = { "dayOfWeek" })
})
@Schema(description = "Horaires d'ouverture du salon par jour")
public class WorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de la ligne d'horaire")
    private Long id;

    @Schema(description = "Jour de la semaine")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 16)
    private DayOfWeek dayOfWeek;

    @Schema(description = "Heure d'ouverture (null si fermé)")
    private LocalTime startTime;

    @Schema(description = "Heure de fermeture (null si fermé)")
    private LocalTime endTime;

    @Schema(description = "Indique si le salon est fermé ce jour-là")
    @Column(nullable = false)
    private boolean closed = false;

    public WorkingHours() {
    }

    @PrePersist
    @PreUpdate
    private void validateTimes() {
        if (closed) {
            // s'il est fermé, on tolère start/end null
            return;
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("startTime et endTime sont requis quand closed = false");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime doit être strictement après startTime");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

}
