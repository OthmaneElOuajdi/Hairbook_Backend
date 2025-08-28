package com.hairbook.validation;

import com.hairbook.util.BelgianValidationUtils;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Valide qu'une chaîne représente un numéro de téléphone belge.
 * <p>
 * - Si {@code mobileOnly=true}, n'accepte que les numéros mobiles belges.<br>
 * - Null/blank est considéré comme valide : utilisez @NotBlank/@NotNull pour contraindre la présence.
 * <p>
 * Exemples valides :
 * <ul>
 *   <li>+32 478 12 34 56</li>
 *   <li>0478 12 34 56</li>
 *   <li>+32 2 123 45 67 (si mobileOnly=false)</li>
 * </ul>
 */
@Documented
@Constraint(validatedBy = ValidBelgianPhone.BelgianPhoneValidator.class)
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.ANNOTATION_TYPE,
    ElementType.PARAMETER,
    ElementType.TYPE_USE // permet sur des types génériques, records, etc.
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBelgianPhone {

    String message() default
        "Le numéro de téléphone doit être un numéro belge valide (ex: +32 4XX XX XX XX ou 04XX XX XX XX)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Si true, n'accepte que les numéros mobiles belges (GSM).
     * Si false, accepte mobiles ET fixes.
     */
    boolean mobileOnly() default false;

    // --- Implémentation du validator ---
    class BelgianPhoneValidator implements ConstraintValidator<ValidBelgianPhone, String> {

        private boolean mobileOnly;

        @Override
        public void initialize(ValidBelgianPhone constraintAnnotation) {
            this.mobileOnly = constraintAnnotation.mobileOnly();
        }

        @Override
        public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
            // Laisse @NotNull/@NotBlank gérer la présence
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return true;
            }
            return mobileOnly
                ? BelgianValidationUtils.isValidBelgianMobileNumber(phoneNumber)
                : BelgianValidationUtils.isValidBelgianPhoneNumber(phoneNumber);
        }
    }
}
