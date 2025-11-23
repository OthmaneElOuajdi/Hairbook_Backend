package com.hairbook.service;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Service de paiement basé sur Stripe Checkout.
 * <p>
 * Responsabilités :
 * <ul>
 *   <li>Création d'une session Stripe Checkout pour un rendez-vous</li>
 *   <li>Gestion des webhooks Stripe (accusé & mise à jour d'état)</li>
 *   <li>Marquage d'un paiement comme réussi et confirmation du rendez-vous</li>
 * </ul>
 * <p>
 * Hypothèses :
 * <ul>
 *   <li>Le prix du service est stocké en centimes d'euro (integer) côté domaine</li>
 *   <li>Un seul paiement par rendez-vous (vérification de doublon)</li>
 *   <li>Le frontend gère les redirections <i>success/cancel</i></li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final AuditService auditService;
    private final EmailService emailService;
    private final UserService userService;

    /** Clé secrète Stripe (mode test/production), injectée via configuration. */
    @Value("${stripe.api-key}")
    private String stripeApiKey;

    /** URL de base du frontend pour les retours success/cancel. */
    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * Initialise le SDK Stripe avec la clé API au démarrage du contexte.
     * Requis par {@link com.stripe.Stripe#apiKey}.
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Crée une session Stripe Checkout pour payer un rendez-vous donné.
     * <ul>
     *   <li>Vérifie l'existence du rendez-vous et l'appartenance à l'utilisateur</li>
     *   <li>Empêche la création si un paiement existe déjà pour ce rendez-vous</li>
     *   <li>Construit la session en mode paiement unique avec une ligne (service)</li>
     *   <li>Persiste un enregistrement {@link Payment} en statut {@link PaymentStatus#PENDING}</li>
     * </ul>
     *
     * @param request   dto contenant l'ID du rendez-vous cible
     * @param userEmail email du demandeur (doit être le propriétaire du rendez-vous)
     * @return objet contenant l'ID de session Stripe et l'URL publique Checkout
     * @throws StripeException problème de communication avec Stripe
     * @throws IllegalArgumentException si rendez-vous introuvable, accès interdit, ou paiement déjà existant
     */
    @Transactional
    public CheckoutResponse createCheckoutSession(CreateCheckoutRequest request, String userEmail)
            throws StripeException {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé"));

        if (!appointment.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Accès non autorisé");
        }

        if (paymentRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new IllegalArgumentException("Un paiement existe déjà pour ce rendez-vous");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payment/cancel?appointment_id=" + appointment.getId())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount((long) appointment.getService().getPriceCents())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(appointment.getService().getName())
                                        .setDescription("Rendez-vous avec " + appointment.getStaffMember().getFullName())
                                        .build())
                                .build())
                        .build())
                .putMetadata("appointmentId", appointment.getId().toString())
                .putMetadata("userId", appointment.getUser().getId().toString())
                .build();

        Session session = Session.create(params);

        Payment payment = Payment.builder()
                .appointment(appointment)
                .provider("STRIPE")
                .providerSessionId(session.getId())
                .providerPaymentId(session.getPaymentIntent())
                .checkoutUrl(session.getUrl())
                .amountCents(appointment.getService().getPriceCents())
                .currency("EUR")
                .status(PaymentStatus.PENDING)
                .metadata(new HashMap<>())
                .build();

        paymentRepository.save(payment);
        log.info("Checkout session created for appointment {}: {}", appointment.getId(), session.getId());

        return CheckoutResponse.builder()
                .sessionId(session.getId())
                .url(session.getUrl())
                .build();
    }

    /**
     * Point d'entrée pour traiter un webhook Stripe (événements paiement).
     * <p>
     * Cette méthode peut être utilisée pour ajouter une logique métier supplémentaire
     * lors de la réception d'un webhook Stripe (logs d'audit, notifications, etc.).
     *
     * @param payload   corps brut du webhook
     * @param sigHeader en-tête {@code Stripe-Signature}
     */
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        log.info("Webhook received - Payload length: {} bytes, Signature present: {}",
                payload != null ? payload.length() : 0,
                sigHeader != null && !sigHeader.isEmpty());

        // Log des informations du webhook pour l'audit
        if (payload != null && payload.contains("checkout.session.completed")) {
            log.info("Checkout session completed event received");
        } else if (payload != null && payload.contains("payment_intent.succeeded")) {
            log.info("Payment intent succeeded event received");
        } else if (payload != null && payload.contains("checkout.session.expired")) {
            log.info("Checkout session expired event received");
        }

        // Ici, on pourrait ajouter d'autres traitements comme :
        // - Enregistrer le webhook brut dans une table d'audit
        // - Envoyer des notifications
        // - Déclencher des événements métier
    }

    /**
     * Marque un paiement comme réussi et confirme le rendez-vous associé.
     * <p>
     * Récupère la {@link Session} Stripe pour compléter l'ID du payment intent si nécessaire,
     * puis passe le paiement à {@link PaymentStatus#SUCCEEDED} et confirme le rendez-vous.
     *
     * @param appointmentId identifiant du rendez-vous (informative log)
     * @param sessionId     identifiant de session Stripe Checkout
     * @throws IllegalArgumentException si aucun paiement n'est lié à la session
     */
    @Transactional
    public void markPaymentSucceeded(UUID appointmentId, String sessionId) {
        Payment payment = paymentRepository.findByProviderSessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé"));

        try {
            Session session = Session.retrieve(sessionId);
            if (session.getPaymentIntent() != null) {
                payment.setProviderPaymentId(session.getPaymentIntent());
            }
        } catch (StripeException e) {
            log.error("Error retrieving Stripe session: {}", e.getMessage());
        }

        payment.markSucceeded();
        paymentRepository.save(payment);

        // Confirmer le rendez-vous
        Appointment appointment = payment.getAppointment();
        appointment.confirm();
        appointmentRepository.save(appointment);

        // Ajouter les points de fidélité au client
        User user = appointment.getUser();
        Integer servicePoints = appointment.getService().getLoyaltyPoints();
        if (servicePoints != null && servicePoints > 0) {
            Integer currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
            user.setLoyaltyPoints(currentPoints + servicePoints);
            userService.updateUser(user.getId(), user);
            log.info("Added {} loyalty points to user {}. New total: {}",
                    servicePoints, user.getEmail(), user.getLoyaltyPoints());
        }

        log.info("Payment succeeded for appointment {}", appointmentId);

        // Envoyer l'email de confirmation de paiement
        double amountPaid = payment.getAmountCents() / 100.0;
        emailService.sendPaymentConfirmation(appointment, amountPaid);

        // Audit log
        auditService.logAction(appointment.getUser().getEmail(), "PAYMENT_SUCCEEDED", "Payment", payment.getId(),
                java.util.Map.of(
                        "appointmentId", appointmentId,
                        "amountCents", payment.getAmountCents(),
                        "sessionId", sessionId
                ));
    }

    /**
     * Marque un paiement comme échoué et envoie un email au client.
     *
     * @param appointmentId identifiant du rendez-vous
     * @param sessionId     identifiant de session Stripe Checkout
     */
    @Transactional
    public void markPaymentFailed(UUID appointmentId, String sessionId) {
        Payment payment = paymentRepository.findByProviderSessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé"));

        payment.markFailed();
        paymentRepository.save(payment);

        Appointment appointment = payment.getAppointment();

        log.info("Payment failed for appointment {}", appointmentId);

        // Envoyer l'email d'échec de paiement
        emailService.sendPaymentFailure(appointment);

        // Audit log
        auditService.logAction(appointment.getUser().getEmail(), "PAYMENT_FAILED", "Payment", payment.getId(),
                java.util.Map.of(
                        "appointmentId", appointmentId,
                        "amountCents", payment.getAmountCents(),
                        "sessionId", sessionId
                ));
    }

    /**
     * Recherche un paiement par fournisseur et identifiant de paiement externe.
     *
     * @param provider          nom du fournisseur (ex: "STRIPE")
     * @param providerPaymentId identifiant du paiement côté fournisseur
     * @return Payment trouvé
     * @throws IllegalArgumentException si le paiement n'existe pas
     */
    public Payment getPaymentByProviderAndPaymentId(String provider, String providerPaymentId) {
        return paymentRepository.findByProviderAndProviderPaymentId(provider, providerPaymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    /**
     * Récupère tous les paiements ayant un statut donné.
     *
     * @param status statut du paiement
     * @return liste des paiements avec ce statut
     */
    public java.util.List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    /**
     * Récupère tous les paiements d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des paiements de l'utilisateur
     */
    public java.util.List<Payment> getUserPayments(UUID userId) {
        return paymentRepository.findByAppointmentUserId(userId);
    }

    /**
     * Récupère tous les paiements en attente.
     *
     * @return liste des paiements PENDING
     */
    public java.util.List<Payment> getPendingPayments() {
        return getPaymentsByStatus(PaymentStatus.PENDING);
    }

    /**
     * Récupère tous les paiements réussis.
     *
     * @return liste des paiements SUCCEEDED
     */
    public java.util.List<Payment> getSucceededPayments() {
        return getPaymentsByStatus(PaymentStatus.SUCCEEDED);
    }

    /**
     * Récupère tous les paiements échoués.
     *
     * @return liste des paiements FAILED
     */
    public java.util.List<Payment> getFailedPayments() {
        return getPaymentsByStatus(PaymentStatus.FAILED);
    }

    /**
     * Récupère le paiement associé à un rendez-vous.
     *
     * @param appointmentId identifiant du rendez-vous
     * @return Payment trouvé ou null si aucun paiement
     */
    public Payment getPaymentByAppointmentId(UUID appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId).orElse(null);
    }

    /**
     * Récupère ou recrée l'URL de paiement pour un rendez-vous en attente.
     * Si le paiement existe avec une URL, la retourne.
     * Sinon, recrée une nouvelle session Stripe.
     *
     * @param appointmentId identifiant du rendez-vous
     * @param userEmail email de l'utilisateur
     * @return CheckoutResponse avec l'URL de paiement
     * @throws StripeException si erreur Stripe
     * @throws IllegalArgumentException si rendez-vous non trouvé ou accès refusé
     */
    @Transactional
    public CheckoutResponse getOrCreatePaymentUrl(UUID appointmentId, String userEmail) throws StripeException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé"));

        // Vérifier que le rendez-vous appartient à l'utilisateur
        if (!appointment.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Accès non autorisé");
        }

        // Vérifier que le rendez-vous est en attente
        if (appointment.getStatus() != be.salon.coiffurereservation.entity.AppointmentStatus.PENDING) {
            throw new IllegalArgumentException("Le rendez-vous n'est pas en attente de paiement");
        }

        // Chercher le paiement existant
        Optional<Payment> existingPayment = paymentRepository.findByAppointmentId(appointmentId);

        // Si le paiement existe et a une URL valide, la retourner
        if (existingPayment.isPresent() && existingPayment.get().getCheckoutUrl() != null) {
            Payment payment = existingPayment.get();
            return CheckoutResponse.builder()
                    .sessionId(payment.getProviderSessionId())
                    .url(payment.getCheckoutUrl())
                    .build();
        }

        // Sinon, créer une nouvelle session (ou mettre à jour l'existante)
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payment/cancel?appointment_id=" + appointment.getId())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount((long) appointment.getService().getPriceCents())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(appointment.getService().getName())
                                        .setDescription("Rendez-vous avec " + appointment.getStaffMember().getFullName())
                                        .build())
                                .build())
                        .build())
                .putMetadata("appointmentId", appointment.getId().toString())
                .putMetadata("userId", appointment.getUser().getId().toString())
                .build();

        Session session = Session.create(params);

        // Mettre à jour ou créer le paiement
        Payment payment;
        if (existingPayment.isPresent()) {
            payment = existingPayment.get();
            payment.setProviderSessionId(session.getId());
            payment.setCheckoutUrl(session.getUrl());
            payment.setProviderPaymentId(session.getPaymentIntent());
        } else {
            payment = Payment.builder()
                    .appointment(appointment)
                    .provider("STRIPE")
                    .providerSessionId(session.getId())
                    .providerPaymentId(session.getPaymentIntent())
                    .checkoutUrl(session.getUrl())
                    .amountCents(appointment.getService().getPriceCents())
                    .currency("EUR")
                    .status(PaymentStatus.PENDING)
                    .metadata(new HashMap<>())
                    .build();
        }

        paymentRepository.save(payment);
        log.info("Payment URL created/updated for appointment {}: {}", appointment.getId(), session.getUrl());

        return CheckoutResponse.builder()
                .sessionId(session.getId())
                .url(session.getUrl())
                .build();
    }
}
