package com.hairbook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service d'envoi d'emails liés aux rendez-vous.
 * <p>
 * Gère l'envoi asynchrone des emails de confirmation, d'annulation et de rappel.
 * Utilise {@link JavaMailSender} avec des messages texte simples. Les URLs front sont
 * construites à partir de {@code app.frontend.url}.
 * <br>
 * Nécessite l'activation d'async avec {@code @EnableAsync} sur une classe de configuration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageService messageService;

    /**
     * Adresse expéditrice utilisée pour tous les emails.
     * Valeur par défaut: {@code noreply@salon.be}.
     */
    @Value("${spring.mail.username:noreply@salon.be}")
    private String fromEmail;

    /**
     * URL de base du frontend (ex: {@code https://app.salon.be}).
     * Utilisée pour construire les liens dans les emails.
     */
    @Value("${app.frontend.url}")
    private String frontendUrl;

    /** Format d'affichage des dates-heures dans les emails (ex: 31/12/2025 at 14:30). */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm");

    /**
     * Envoie un email de confirmation pour un rendez-vous donné.
     * <ul>
     *   <li>Sujet: {@code Appointment Confirmation}</li>
     *   <li>Contenu: service, date/heure, durée, staff, prix, lien de gestion</li>
     * </ul>
     *
     * @param appointment rendez-vous confirmé
     */
    @Async
    public void sendAppointmentConfirmation(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Appointment Confirmation");

            String body = String.format("""
                    Hello %s,

                    Your appointment has been successfully confirmed.

                    Details:
                    - Service: %s
                    - Date & Time: %s
                    - Duration: %d minutes
                    - With: %s
                    - Price: %.2f €

                    To modify or cancel your appointment, please log in to your account:
                    %s

                    See you soon!

                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    appointment.getService().getName(),
                    LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()).format(DATE_FORMATTER),
                    appointment.getService().getDurationMinutes(),
                    appointment.getStaffMember().getFullName(),
                    appointment.getService().getPriceEuros(),
                    frontendUrl + "/appointments");

            message.setText(body);
            mailSender.send(message);
            log.info("Confirmation email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email", e);
        }
    }

    /**
     * Envoie un email d'annulation pour un rendez-vous donné.
     * <ul>
     *   <li>Sujet: {@code Appointment Cancellation}</li>
     *   <li>Contenu: service, date/heure, staff, raison d'annulation, lien de nouvelle réservation</li>
     * </ul>
     *
     * @param appointment rendez-vous annulé
     */
    @Async
    public void sendAppointmentCancellation(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Appointment Cancellation");

            String body = String.format("""
                    Hello %s,

                    Your appointment has been cancelled.

                    Cancelled appointment details:
                    - Service: %s
                    - Date & Time: %s
                    - Staff member: %s

                    Reason: %s

                    You can book a new appointment anytime:
                    %s

                    Best regards,
                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    appointment.getService().getName(),
                    LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()).format(DATE_FORMATTER),
                    appointment.getStaffMember().getFullName(),
                    appointment.getCancellationReason() != null ? appointment.getCancellationReason() : "Not specified",
                    frontendUrl + "/booking");

            message.setText(body);
            mailSender.send(message);
            log.info("Cancellation email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send cancellation email", e);
        }
    }

    /**
     * Envoie un email de rappel (J-1) pour un rendez-vous donné.
     * <ul>
     *   <li>Sujet: {@code Reminder: Appointment Tomorrow}</li>
     *   <li>Contenu: service, date/heure, durée, staff</li>
     * </ul>
     *
     * @param appointment rendez-vous à rappeler
     */
    @Async
    public void sendAppointmentReminder(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Reminder: Appointment Tomorrow");

            String body = String.format("""
                    Hello %s,

                    This is a reminder that you have an appointment tomorrow.

                    Details:
                    - Service: %s
                    - Date & Time: %s
                    - Duration: %d minutes
                    - With: %s

                    We are looking forward to seeing you!

                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    appointment.getService().getName(),
                    LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()).format(DATE_FORMATTER),
                    appointment.getService().getDurationMinutes(),
                    appointment.getStaffMember().getFullName());

            message.setText(body);
            mailSender.send(message);
            log.info("Reminder email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send reminder email", e);
        }
    }

    /**
     * Envoie un email d'échec de paiement.
     * <ul>
     *   <li>Sujet: {@code Payment Failed - Appointment Not Confirmed}</li>
     *   <li>Contenu: service, date/heure, lien pour réessayer</li>
     * </ul>
     *
     * @param appointment rendez-vous dont le paiement a échoué
     */
    @Async
    public void sendPaymentFailure(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Payment Failed - Appointment Not Confirmed");

            String body = String.format("""
                    Hello %s,

                    Unfortunately, your payment could not be processed.

                    Appointment Details:
                    - Service: %s
                    - Date & Time: %s
                    - Duration: %d minutes
                    - With: %s
                    - Amount: %.2f €

                    Your appointment is currently on hold with status PENDING.
                    
                    Please try again to complete your payment:
                    %s

                    If you continue to experience issues, please contact us.

                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    appointment.getService().getName(),
                    LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()).format(DATE_FORMATTER),
                    appointment.getService().getDurationMinutes(),
                    appointment.getStaffMember().getFullName(),
                    appointment.getService().getPriceEuros(),
                    frontendUrl + "/appointments");

            message.setText(body);
            mailSender.send(message);
            log.info("Payment failure email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send payment failure email", e);
        }
    }

    /**
     * Envoie un email de confirmation de paiement réussi.
     * <ul>
     *   <li>Sujet: {@code Payment Confirmation - Appointment Confirmed}</li>
     *   <li>Contenu: service, date/heure, durée, staff, montant payé, statut de confirmation</li>
     * </ul>
     *
     * @param appointment rendez-vous dont le paiement a été confirmé
     * @param amountPaid  montant payé en euros
     */
    @Async
    public void sendPaymentConfirmation(Appointment appointment, double amountPaid) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(appointment.getUser().getEmail());
            message.setSubject("Payment Confirmation - Appointment Confirmed");

            String body = String.format("""
                    Hello %s,

                    Your payment has been successfully processed and your appointment is now confirmed!

                    Payment Details:
                    - Amount Paid: %.2f €
                    - Payment Status: Confirmed
                    
                    Appointment Details:
                    - Service: %s
                    - Date & Time: %s
                    - Duration: %d minutes
                    - With: %s

                    You can view your appointment details at any time:
                    %s

                    Thank you for your payment!

                    The Salon Team
                    """,
                    appointment.getUser().getFirstName(),
                    amountPaid,
                    appointment.getService().getName(),
                    LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()).format(DATE_FORMATTER),
                    appointment.getService().getDurationMinutes(),
                    appointment.getStaffMember().getFullName(),
                    frontendUrl + "/appointments");

            message.setText(body);
            mailSender.send(message);
            log.info("Payment confirmation email sent to {}", appointment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email", e);
        }
    }

    /**
     * Envoie un email de bienvenue avec lien de vérification.
     * <ul>
     *   <li>Sujet: {@code Welcome! Please Verify Your Email}</li>
     *   <li>Contenu: message de bienvenue, lien de vérification</li>
     * </ul>
     *
     * @param email email du nouvel utilisateur
     * @param firstName prénom de l'utilisateur
     * @param verificationToken token de vérification
     */
    @Async
    public void sendWelcomeEmail(String email, String firstName, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Bienvenue! Veuillez vérifier votre email");

            String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;

            String body = String.format("""
                    Bonjour %s,

                    Bienvenue chez Salon de Coiffure! 🎉

                    Nous sommes ravis de vous compter parmi nos clients.

                    Pour finaliser votre inscription et profiter pleinement de nos services,
                    veuillez vérifier votre adresse email en cliquant sur le lien ci-dessous:

                    %s

                    Ce lien est valide pendant 24 heures.

                    Une fois votre email vérifié, vous pourrez:
                    ✓ Prendre rendez-vous en ligne
                    ✓ Consulter l'historique de vos rendez-vous
                    ✓ Gérer votre profil

                    Si vous n'avez pas créé de compte, vous pouvez ignorer cet email.

                    À bientôt!

                    L'équipe du Salon
                    """,
                    firstName,
                    verificationLink);

            message.setText(body);
            mailSender.send(message);
            log.info("Welcome email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    /**
     * Envoie un email simple avec sujet et corps personnalisés.
     *
     * @param to      destinataire
     * @param subject sujet de l'email
     * @param body    corps de l'email
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Simple email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}", to, e);
        }
    }

    /**
     * Envoie un email avec les identifiants de connexion à un nouvel utilisateur créé par un admin.
     * <ul>
     *   <li>Sujet: {@code Votre compte a été créé}</li>
     *   <li>Contenu: email, mot de passe temporaire, lien de connexion</li>
     * </ul>
     *
     * @param email email de l'utilisateur
     * @param firstName prénom de l'utilisateur
     * @param password mot de passe temporaire
     */
    @Async
    public void sendNewAccountCredentials(String email, String firstName, String password) {
        log.info("Attempting to send credentials email to: {}", email);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Votre compte a été créé - Identifiants de connexion");
            log.info("Email message created for: {}", email);

            String body = String.format("""
                    Bonjour %s,
                    
                    Un compte a ete cree pour vous sur notre plateforme de gestion du salon.
                    
                    Vos identifiants de connexion:
                    Email: %s
                    Mot de passe: %s
                    
                    IMPORTANT: Pour des raisons de securite, nous vous recommandons vivement
                    de changer votre mot de passe lors de votre premiere connexion.
                    
                    Vous pouvez vous connecter des maintenant:
                    %s
                    
                    Une fois connecte, vous aurez acces aux fonctionnalites correspondant a votre role.
                    
                    Si vous n'etes pas a l'origine de cette creation de compte,
                    veuillez nous contacter immediatement.
                    
                    A bientot!
                    
                    L'equipe du Salon
                    """,
                    firstName,
                    email,
                    password,
                    frontendUrl + "/login");

            message.setText(body);
            mailSender.send(message);
            log.info("New account credentials email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send credentials email to {}", email, e);
        }
    }
}
