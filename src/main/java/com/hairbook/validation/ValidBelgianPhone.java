package com.hairbook.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Valide qu'une chaîne représente un numéro de téléphone belge (et uniquement
 * belge).
 *
 * <p>
 * Formats acceptés (avec espaces/tirets/points/parenthèses permis) :
 * </p>
 * <ul>
 * <li>Nationaux : <code>0...</code> (ex. <code>0478 12 34 56</code>,
 * <code>02 123 45 67</code>)</li>
 * <li>Internationaux : <code>+32...</code> (ex. <code>+32 478 12 34 56</code>,
 * <code>+32 2 123 45 67</code>)</li>
 * </ul>
 *
 * <p>
 * Règles simplifiées conformes au plan de numérotation belge :
 * </p>
 * <ul>
 * <li><b>Mobiles</b> : commencent par <code>04</code> (national) ou
 * <code>+324</code> (international) et ont 10 (national) ou 9 (international)
 * chiffres significatifs.</li>
 * <li><b>Fixes</b> : commencent par <code>0</code> suivi d'un chiffre <em>≠
 * 4</em> (national), ou par <code>+32</code> suivi d'un chiffre <em>≠ 4</em>
 * (international).</li>
 * <li>Les numéros non belges sont rejetés.</li>
 * <li><code>null</code> / vide est considéré comme valide — utilisez
 * {@code @NotBlank/@NotNull} si la présence est requise.</li>
 * </ul>
 *
 * <p>
 * Exemples valides :
 * </p>
 * <ul>
 * <li><code>+32 478 12 34 56</code></li>
 * <li><code>0478 12 34 56</code></li>
 * <li><code>+32 2 123 45 67</code></li>
 * <li><code>02 123 45 67</code></li>
 * </ul>
 */
@Documented
@Constraint(validatedBy = ValidBelgianPhone.BelgianPhoneValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER,
        ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBelgianPhone {

    String message() default "Le numéro doit être un numéro belge valide (ex: +32 4XX XX XX XX, 04XX XX XX XX, +32 2 XXX XX XX, 02 XXX XX XX)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Si true, n'accepte que les numéros mobiles belges (GSM).
     * Si false, accepte mobiles ET fixes — mais toujours belges.
     */
    boolean mobileOnly() default false;

    // --- Implémentation du validator ---
    class BelgianPhoneValidator implements ConstraintValidator<ValidBelgianPhone, String> {

        private boolean mobileOnly;

        // Regex nationales/internationales après nettoyage
        // Mobiles : 04 + 8 chiffres (national) ou +324 + 8 chiffres (international)
        private static final String MOBILE_NATIONAL = "^04\\d{8}$";
        private static final String MOBILE_INTL = "^\\+324\\d{8}$";

        // Fixes : 0 + (≠4) + 7 chiffres => total 9 (ex : 02 123 45 67)
        // +32 + (≠4) + 7 chiffres => total 8 après code pays (ex : +32 2 123 45 67)
        private static final String FIXED_NATIONAL = "^0[1-35-9]\\d{7}$";
        private static final String FIXED_INTL = "^\\+32[1-35-9]\\d{7}$";

        @Override
        public void initialize(ValidBelgianPhone constraintAnnotation) {
            this.mobileOnly = constraintAnnotation.mobileOnly();
        }

        @Override
        public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return true; // présence gérée par @NotBlank/@NotNull si nécessaire
            }

            // Nettoyage des séparateurs (espaces, tirets, points, parenthèses)
            String cleaned = phoneNumber.replaceAll("[\\s\\-\\.\\(\\)]", "");

            if (mobileOnly) {
                // Uniquement mobiles belges
                return cleaned.matches(MOBILE_NATIONAL) || cleaned.matches(MOBILE_INTL);
            }

            // Mobiles OU fixes belges
            return cleaned.matches(MOBILE_NATIONAL)
                    || cleaned.matches(MOBILE_INTL)
                    || cleaned.matches(FIXED_NATIONAL)
                    || cleaned.matches(FIXED_INTL);
        }
    }
}
