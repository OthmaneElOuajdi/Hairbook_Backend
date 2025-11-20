package com.hairbook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les tâches planifiées liées aux rendez-vous.
 * Notamment l'annulation automatique des rendez-vous non payés.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentSchedulerService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final AuditService auditService;

    /**
     * Annule automatiquement les rendez-vous PENDING dont le paiement a échoué
     * et qui ont été créés il y a plus de 15 minutes.
     *
     * Exécuté toutes les 5 minutes.
     */
    @Scheduled(fixedRate = 300000) // Toutes les 5 minutes (300 000 ms)
    @Transactional
    public void cancelUnpaidAppointments() {
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

        // Trouver tous les paiements échoués créés il y a plus de 15 minutes
        List<Payment> failedPayments = paymentRepository.findByStatus(PaymentStatus.FAILED);

        for (Payment payment : failedPayments) {
            Appointment appointment = payment.getAppointment();

            // Vérifier si le rendez-vous est toujours PENDING et créé il y a plus de 15 min
            if (appointment.getStatus() == AppointmentStatus.PENDING
                    && appointment.getCreatedAt().isBefore(fifteenMinutesAgo)) {

                // Annuler le rendez-vous
                appointment.cancel("Paiement non effectué dans les 15 minutes");
                appointmentRepository.save(appointment);

                log.info("Rendez-vous {} annulé automatiquement - paiement non effectué",
                        appointment.getId());

                // Audit log
                auditService.logAction(
                        appointment.getUser().getEmail(),
                        "APPOINTMENT_AUTO_CANCELLED",
                        "Appointment",
                        appointment.getId(),
                        java.util.Map.of(
                                "reason", "Paiement non effectué dans les 15 minutes",
                                "paymentStatus", payment.getStatus().toString()
                        )
                );
            }
        }
    }

    /**
     * Annule automatiquement les rendez-vous PENDING sans paiement associé
     * créés il y a plus de 15 minutes.
     *
     * Exécuté toutes les 5 minutes.
     */
    @Scheduled(fixedRate = 300000) // Toutes les 5 minutes
    @Transactional
    public void cancelPendingAppointmentsWithoutPayment() {
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

        // Trouver tous les rendez-vous PENDING créés il y a plus de 15 minutes
        List<Appointment> pendingAppointments = appointmentRepository
                .findByStatusAndCreatedAtBefore(AppointmentStatus.PENDING, fifteenMinutesAgo);

        for (Appointment appointment : pendingAppointments) {
            // Vérifier s'il n'y a pas de paiement réussi
            Optional<Payment> paymentOpt = paymentRepository.findByAppointmentId(appointment.getId());

            boolean hasSuccessfulPayment = paymentOpt.isPresent()
                    && paymentOpt.get().getStatus() == PaymentStatus.SUCCEEDED;

            if (!hasSuccessfulPayment) {
                appointment.cancel("Aucun paiement reçu dans les 15 minutes");
                appointmentRepository.save(appointment);

                log.info("Rendez-vous {} annulé automatiquement - aucun paiement",
                        appointment.getId());

                // Audit log
                auditService.logAction(
                        appointment.getUser().getEmail(),
                        "APPOINTMENT_AUTO_CANCELLED",
                        "Appointment",
                        appointment.getId(),
                        java.util.Map.of("reason", "Aucun paiement reçu dans les 15 minutes")
                );
            }
        }
    }
}
