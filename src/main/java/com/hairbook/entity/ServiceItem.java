package com.hairbook.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_items")
@Schema(description = "Représente un service du salon de coiffure (ex: coupe, coloration)")
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du service")
    private Long id;

    @Schema(description = "Nom du service (ex: Coupe femme, Coloration, Brushing)")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Prix du service en euros")
    @Column(nullable = false)
    private double price;

    @Schema(description = "Nombre de points de fidélité offerts lors de ce service")
    @Column(nullable = false)
    private int loyaltyPointsReward;

    @Schema(description = "Description détaillée du service")
    private String description;

    public ServiceItem() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLoyaltyPointsReward() {
        return loyaltyPointsReward;
    }

    public void setLoyaltyPointsReward(int loyaltyPointsReward) {
        this.loyaltyPointsReward = loyaltyPointsReward;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
