package com.hairbook.util;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utilitaires de validation spécifiques à la Belgique.
 * 
 * Fournit des méthodes pour valider :
 * - Numéros de téléphone belges (fixe et mobile)
 * - Emails non jetables
 * - Codes postaux belges
 */
public class BelgianValidationUtils {

    // Regex pour numéros GSM belges (04xx ou +32 4xx)
    private static final Pattern BELGIAN_MOBILE_PATTERN = Pattern.compile(
        "^(\\+32\\s?|0)4[0-9]{2}\\s?[0-9]{2}\\s?[0-9]{2}\\s?[0-9]{2}$"
    );

    // Regex pour numéros fixes belges (0x / +32x)
    private static final Pattern BELGIAN_LANDLINE_PATTERN = Pattern.compile(
        "^(\\+32\\s?|0)[1-9][0-9]\\s?[0-9]{2}\\s?[0-9]{2}\\s?[0-9]{2}$"
    );

    // Domaines d’emails jetables connus
    private static final Set<String> DISPOSABLE_EMAIL_DOMAINS = Set.of(
        "10minutemail.com", "guerrillamail.com", "mailinator.com", "tempmail.org",
        "yopmail.com", "temp-mail.org", "throwaway.email", "maildrop.cc",
        "sharklasers.com", "guerrillamail.info", "guerrillamail.biz", "guerrillamail.net",
        "guerrillamail.org", "guerrillamailblock.com", "pokemail.net", "spam4.me",
        "tempail.com", "tempmailaddress.com", "emailondeck.com", "jetable.org",
        "mytrashmail.com", "mailnesia.com", "trashmail.net", "dispostable.com",
        "tempinbox.com", "mohmal.com", "fakeinbox.com", "mailcatch.com",
        "mailnator.com", "tempmailer.com", "temp-mail.io", "disposablemail.com"
    );

    /**
     * Valide un numéro de téléphone belge (mobile ou fixe).
     * @param phoneNumber numéro à valider
     * @return true si valide
     */
    public static boolean isValidBelgianPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phoneNumber.replaceAll("\\s+", " ").trim();
        return BELGIAN_MOBILE_PATTERN.matcher(cleanPhone).matches()
            || BELGIAN_LANDLINE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Valide spécifiquement un numéro GSM belge.
     */
    public static boolean isValidBelgianMobileNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phoneNumber.replaceAll("\\s+", " ").trim();
        return BELGIAN_MOBILE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Formate un numéro belge au format standard (ex: "0478 12 34 56").
     */
    public static String formatBelgianPhoneNumber(String phoneNumber) {
        if (!isValidBelgianPhoneNumber(phoneNumber)) {
            return phoneNumber;
        }
        String digits = phoneNumber.replaceAll("[^0-9+]", "");
        if (digits.startsWith("+32")) {
            digits = "0" + digits.substring(3);
        }
        if (digits.length() == 10) {
            return String.format("%s %s %s %s %s",
                digits.substring(0, 2),
                digits.substring(2, 4),
                digits.substring(4, 6),
                digits.substring(6, 8),
                digits.substring(8, 10)
            );
        }
        return phoneNumber;
    }

    /**
     * Vérifie si un email utilise un domaine jetable.
     */
    public static boolean isDisposableEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String domain = email.toLowerCase().trim();
        int atIndex = domain.lastIndexOf('@');
        if (atIndex == -1 || atIndex == domain.length() - 1) {
            return false;
        }
        domain = domain.substring(atIndex + 1);
        return DISPOSABLE_EMAIL_DOMAINS.contains(domain);
    }

    /**
     * Valide un email en excluant les domaines jetables.
     */
    public static boolean isValidNonDisposableEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return false;
        }
        return !isDisposableEmail(email);
    }

    /**
     * Valide un code postal belge (1000–9999).
     */
    public static boolean isValidBelgianPostalCode(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return false;
        }
        return postalCode.matches("^[1-9][0-9]{3}$");
    }
}
