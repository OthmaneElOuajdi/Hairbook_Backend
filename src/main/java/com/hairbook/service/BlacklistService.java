package com.hairbook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service de gestion de la blacklist.
 * <p>
 * Ce service permet de bannir des emails et numéros de téléphone,
 * et de vérifier si un email ou téléphone est banni.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    /**
     * Vérifie si un email est dans la blacklist.
     *
     * @param email adresse email à vérifier
     * @return true si l'email est banni
     */
    public boolean isEmailBlacklisted(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return blacklistRepository.existsByEmail(email.toLowerCase());
    }

    /**
     * Vérifie si un numéro de téléphone est dans la blacklist.
     *
     * @param phone numéro de téléphone à vérifier
     * @return true si le téléphone est banni
     */
    public boolean isPhoneBlacklisted(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return blacklistRepository.existsByPhone(phone);
    }

    /**
     * Ajoute un utilisateur à la blacklist.
     * Bannit à la fois l'email et le téléphone de l'utilisateur.
     *
     * @param user utilisateur à bannir
     * @param adminId ID de l'admin qui effectue le bannissement
     * @param reason raison du bannissement
     * @return entrée blacklist créée
     */
    @Transactional
    public Blacklist addToBlacklist(User user, UUID adminId, String reason) {
        // Créer l'entrée blacklist
        Blacklist blacklist = Blacklist.builder()
                .email(user.getEmail() != null ? user.getEmail().toLowerCase() : null)
                .phone(user.getPhone())
                .reason(reason)
                .bannedUserId(user.getId())
                .bannedByAdminId(adminId)
                .build();

        Blacklist saved = blacklistRepository.save(blacklist);
        log.info("User {} added to blacklist by admin {}", user.getEmail(), adminId);
        return saved;
    }

    /**
     * Vérifie si un email ou un téléphone est banni.
     * Retourne un message d'erreur approprié.
     *
     * @param email email à vérifier
     * @param phone téléphone à vérifier
     * @return message d'erreur si banni, null sinon
     */
    public String checkBlacklist(String email, String phone) {
        boolean emailBanned = isEmailBlacklisted(email);
        boolean phoneBanned = isPhoneBlacklisted(phone);

        if (emailBanned && phoneBanned) {
            return "L'adresse email et le numéro de téléphone sont bannis du site.";
        } else if (emailBanned) {
            return "L'adresse email est bannie du site.";
        } else if (phoneBanned) {
            return "Le numéro de téléphone est banni du site.";
        }

        return null;
    }
}
