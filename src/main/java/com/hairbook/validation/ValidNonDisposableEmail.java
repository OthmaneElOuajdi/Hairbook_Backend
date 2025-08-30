package com.hairbook.validation;

import com.hairbook.service.BelgianValidationService;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Valide qu'une adresse e-mail n'est PAS jetable.
 * <p>
 * - Null/blank est considéré comme valide : combinez avec @NotBlank/@NotNull
 * pour la présence,
 * et @Email pour le format RFC.
 */
@Documented
@Constraint(validatedBy = ValidNonDisposableEmail.NonDisposableEmailValidator.class)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.PARAMETER,
        ElementType.TYPE_USE // permet l'usage sur des types génériques/records
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNonDisposableEmail {

    String message() default "Les adresses email jetables ne sont pas autorisées. Veuillez utiliser une adresse email permanente.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // --- Implémentation du validator ---
    class NonDisposableEmailValidator implements ConstraintValidator<ValidNonDisposableEmail, String> {

        @Autowired
        private BelgianValidationService validationService;

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context) {
            // Laisse @NotNull/@NotBlank et @Email gérer présence et format
            if (email == null || email.trim().isEmpty()) {
                return true;
            }
            return validationService.validateNonDisposableEmail(email);
        }
    }
}
