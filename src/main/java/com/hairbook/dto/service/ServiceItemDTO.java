package com.hairbook.dto.service;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Service proposé par le salon")
public class ServiceItemDTO {

    @Schema(description = "Identifiant du service")
    private Long id;

    @Schema(description = "Nom du service")
    @NotBlank
    private String name;

    @Schema(description = "Description du service")
    private String description;

    @Schema(description = "Prix TTC")
    @Positive
    private BigDecimal price;

    @Schema(description = "Durée en minutes")
    @Positive
    private Integer durationMinutes;

    @Schema(description = "Points de fidélité gagnés")
    @Positive
    private Integer pointsReward;

    public ServiceItemDTO() {
    }

    public ServiceItemDTO(Long id, String name, String description, BigDecimal price, Integer durationMinutes,
            Integer pointsReward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.pointsReward = pointsReward;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getPointsReward() {
        return pointsReward;
    }

    public void setPointsReward(Integer pointsReward) {
        this.pointsReward = pointsReward;
    }
}
