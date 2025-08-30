package com.hairbook.dto.user;

import com.hairbook.validation.ValidBelgianPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la mise à jour d'un utilisateur
 */
@Schema(description = "Données pour mettre à jour un utilisateur")
public class UserUpdateDTO {

    @Schema(description = "Nom complet de l'utilisateur")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String fullName;

    @Schema(description = "Nouveau mot de passe")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    @Schema(description = "Numéro de téléphone belge")
    @ValidBelgianPhone(mobileOnly = false)
    private String phone;

    public UserUpdateDTO() {
    }

    public UserUpdateDTO(String fullName, String password, String phone) {
        this.fullName = fullName;
        this.password = password;
        this.phone = phone;
    }

    // Getters et Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
