package com.hairbook.validation;

import com.hairbook.service.BelgianValidationService;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Annotation de validation personnalisée qui vérifie qu'une adresse email
 * n'est pas issue d'un fournisseur "jetable" (disposable email).
 *
 * <p>
 * Caractéristiques :
 * </p>
 * <ul>
 * <li>Une valeur {@code null} ou vide est considérée comme valide.
 * Utilisez {@link jakarta.validation.constraints.NotBlank} /
 * {@link jakarta.validation.constraints.NotNull} pour exiger la présence.</li>
 * <li>Combinez avec {@link jakarta.validation.constraints.Email} pour vérifier
 * le format RFC.</li>
 * <li>Le contrôle de la "jetabilité" est délégué à
 * {@link BelgianValidationService}.</li>
 * </ul>
 *
 * <p>
 * Exemple d’utilisation :
 * </p>
 * 
 * <pre>
 * {
 *     &#64;code
 *     public class UserRegistrationDTO {
 *
 *         &#64;Email
 *         &#64;NotBlank
 *         @ValidNonDisposableEmail
 *         private String email;
 *     }
 * }
 * </pre>
 *
 * <p>
 * Si la validation échoue, le message par défaut est :
 * <i>"Les adresses email jetables ne sont pas autorisées. Veuillez utiliser une
 * adresse email permanente."</i>
 * </p>
 */
@Documented
@Constraint(validatedBy = ValidNonDisposableEmail.NonDisposableEmailValidator.class)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNonDisposableEmail {

    /**
     * Message affiché lorsque l'adresse email est jugée jetable.
     */
    String message() default "Les adresses email jetables ne sont pas autorisées. Veuillez utiliser une adresse email permanente.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Validator interne qui délègue la logique métier à
     * {@link BelgianValidationService}.
     */
    class NonDisposableEmailValidator implements ConstraintValidator<ValidNonDisposableEmail, String> {

        @Autowired
        private BelgianValidationService validationService;

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context) {
            if (email == null || email.trim().isEmpty()) {
                return true;
            }
            return validationService.validateNonDisposableEmail(email);
        }
    }
}
