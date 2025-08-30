package com.hairbook.service;

/**
 * Service fournissant des méthodes de validation spécifiques aux règles et
 * formats belges.
 * Inclut la validation pour les numéros de téléphone, les codes postaux et les
 * adresses e-mail.
 */
public interface BelgianValidationService {

    /**
     * Valide si une chaîne de caractères correspond au format d'un numéro de
     * téléphone belge (fixe ou mobile).
     *
     * @param phoneNumber Le numéro de téléphone à valider.
     * @return {@code true} si le numéro est valide, sinon {@code false}.
     */
    boolean validateBelgianPhoneNumber(String phoneNumber);

    /**
     * Valide le format d'une adresse e-mail et vérifie qu'elle n'appartient pas à
     * un domaine de messagerie jetable connu.
     *
     * @param email L'adresse e-mail à valider.
     * @return {@code true} si l'e-mail est valide et non jetable, sinon
     *         {@code false}.
     */
    boolean validateNonDisposableEmail(String email);

    /**
     * Formate un numéro de téléphone belge en un format standard international (ex:
     * +32xxxxxxxxx).
     *
     * @param phoneNumber Le numéro de téléphone à formater.
     * @return Le numéro formaté, ou la chaîne originale en cas d'échec.
     */
    String formatBelgianPhoneNumber(String phoneNumber);

    /**
     * Valide si une chaîne de caractères correspond à un code postal belge (4
     * chiffres).
     *
     * @param postalCode Le code postal à valider.
     * @return {@code true} si le code postal est valide, sinon {@code false}.
     */
    boolean validateBelgianPostalCode(String postalCode);

    /**
     * Vérifie si le domaine d'une adresse e-mail est répertorié comme un
     * fournisseur de messagerie jetable.
     *
     * @param email L'adresse e-mail à vérifier.
     * @return {@code true} si le domaine est jetable, sinon {@code false}.
     */
    boolean isDisposableEmailDomain(String email);

    /**
     * Valide si une chaîne de caractères correspond au format d'un numéro de
     * téléphone mobile belge.
     *
     * @param phoneNumber Le numéro de téléphone à valider.
     * @return {@code true} si le numéro de mobile est valide, sinon {@code false}.
     */
    boolean validateBelgianMobileNumber(String phoneNumber);
}
