package com.hairbook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundRequestService {

    private final RefundRequestRepository refundRequestRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${file.upload.dir:uploads/refund-justifications}")
    private String uploadDir;

    /**
     * Crée une nouvelle demande de remboursement (par email).
     */
    @Transactional
    public RefundRequestDTO createRefundRequestByEmail(String appointmentIdStr, String userEmail, String reason, MultipartFile file) {
        // Convertir l'ID du rendez-vous
        UUID appointmentId = UUID.fromString(appointmentIdStr);

        // Trouver l'utilisateur par email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        UUID userId = user.getId();

        // Vérifier que le rendez-vous existe
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé"));

        // Vérifier que le rendez-vous appartient à l'utilisateur
        if (!appointment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Ce rendez-vous ne vous appartient pas");
        }

        // Vérifier qu'il n'y a pas déjà une demande en attente
        refundRequestRepository.findPendingByAppointmentId(appointmentId)
                .ifPresent(r -> {
                    throw new IllegalArgumentException("Une demande de remboursement est déjà en cours pour ce rendez-vous");
                });

        // Vérifier que le rendez-vous est dans moins de 24h
        LocalDate appointmentDate = appointment.getAppointmentDate();
        LocalDateTime appointmentTime = LocalDateTime.of(appointmentDate, appointment.getStartTime());
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilAppointment = java.time.Duration.between(now, appointmentTime).toHours();

        if (hoursUntilAppointment >= 24) {
            throw new IllegalArgumentException("Vous pouvez annuler gratuitement ce rendez-vous depuis votre espace client");
        }

        if (hoursUntilAppointment < 0) {
            throw new IllegalArgumentException("Ce rendez-vous est déjà passé");
        }

        // Sauvegarder le fichier justificatif
        String filePath = null;
        String fileName = null;
        if (file != null && !file.isEmpty()) {
            try {
                fileName = saveFile(file, appointmentIdStr);
                filePath = uploadDir + "/" + fileName;
            } catch (IOException e) {
                log.error("Erreur lors de la sauvegarde du fichier", e);
                throw new RuntimeException("Erreur lors de la sauvegarde du fichier justificatif");
            }
        }

        // Créer la demande de remboursement
        RefundRequest refundRequest = RefundRequest.builder()
                .appointment(appointment)
                .user(user)
                .reason(reason)
                .justificationFilePath(filePath)
                .justificationFileName(fileName)
                .status(RefundStatus.PENDING)
                .build();

        refundRequest = refundRequestRepository.save(refundRequest);

        log.info("Demande de remboursement créée: {} pour le rendez-vous: {}", refundRequest.getId(), appointmentIdStr);

        // Envoyer un email à l'admin
        sendAdminNotification(refundRequest);

        return toDTO(refundRequest);
    }

    /**
     * Récupère toutes les demandes de remboursement en attente.
     */
    @Transactional(readOnly = true)
    public List<RefundRequestDTO> getAllPendingRequests() {
        return refundRequestRepository.findAllPending().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les demandes de remboursement d'un utilisateur (par UUID).
     */
    @Transactional(readOnly = true)
    public List<RefundRequestDTO> getUserRefundRequests(String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        return refundRequestRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les demandes de remboursement d'un utilisateur (par email).
     */
    @Transactional(readOnly = true)
    public List<RefundRequestDTO> getUserRefundRequestsByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        return refundRequestRepository.findByUserId(user.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Approuve une demande de remboursement (par email).
     */
    @Transactional
    public RefundRequestDTO approveRefundRequestByEmail(String refundRequestId, String adminEmail, String adminComment) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande de remboursement non trouvée"));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trouvé"));

        refundRequest.setStatus(RefundStatus.APPROVED);
        refundRequest.setAdminComment(adminComment);
        refundRequest.setProcessedBy(admin);
        refundRequest.setProcessedAt(LocalDateTime.now());

        refundRequest = refundRequestRepository.save(refundRequest);

        log.info("Demande de remboursement approuvée: {} par l'admin: {}", refundRequestId, adminEmail);

        // Annuler le rendez-vous pour libérer le créneau
        Appointment appointment = refundRequest.getAppointment();
        appointment.setStatus(be.salon.coiffurereservation.entity.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        log.info("Rendez-vous {} annulé suite à l'approbation du remboursement", appointment.getId());

        // Envoyer un email au client
        sendApprovalEmail(refundRequest);

        return toDTO(refundRequest);
    }

    /**
     * Approuve une demande de remboursement (par UUID).
     */
    @Transactional
    public RefundRequestDTO approveRefundRequest(String refundRequestId, String adminIdStr, String adminComment) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande de remboursement non trouvée"));

        UUID adminId = UUID.fromString(adminIdStr);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trouvé"));

        refundRequest.setStatus(RefundStatus.APPROVED);
        refundRequest.setAdminComment(adminComment);
        refundRequest.setProcessedBy(admin);
        refundRequest.setProcessedAt(LocalDateTime.now());

        refundRequest = refundRequestRepository.save(refundRequest);

        log.info("Demande de remboursement approuvée: {} par l'admin: {}", refundRequestId, adminIdStr);

        // Annuler le rendez-vous pour libérer le créneau
        Appointment appointment = refundRequest.getAppointment();
        appointment.setStatus(be.salon.coiffurereservation.entity.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        log.info("Rendez-vous {} annulé suite à l'approbation du remboursement", appointment.getId());

        // Envoyer un email au client
        sendApprovalEmail(refundRequest);

        return toDTO(refundRequest);
    }

    /**
     * Rejette une demande de remboursement (par email).
     */
    @Transactional
    public RefundRequestDTO rejectRefundRequestByEmail(String refundRequestId, String adminEmail, String adminComment) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande de remboursement non trouvée"));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trouvé"));

        refundRequest.setStatus(RefundStatus.REJECTED);
        refundRequest.setAdminComment(adminComment);
        refundRequest.setProcessedBy(admin);
        refundRequest.setProcessedAt(LocalDateTime.now());

        refundRequest = refundRequestRepository.save(refundRequest);

        log.info("Demande de remboursement rejetée: {} par l'admin: {}", refundRequestId, adminEmail);

        // Envoyer un email au client
        sendRejectionEmail(refundRequest);

        return toDTO(refundRequest);
    }

    /**
     * Rejette une demande de remboursement (par UUID).
     */
    @Transactional
    public RefundRequestDTO rejectRefundRequest(String refundRequestId, String adminIdStr, String adminComment) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande de remboursement non trouvée"));

        UUID adminId = UUID.fromString(adminIdStr);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trouvé"));

        refundRequest.setStatus(RefundStatus.REJECTED);
        refundRequest.setAdminComment(adminComment);
        refundRequest.setProcessedBy(admin);
        refundRequest.setProcessedAt(LocalDateTime.now());

        refundRequest = refundRequestRepository.save(refundRequest);

        log.info("Demande de remboursement rejetée: {} par l'admin: {}", refundRequestId, adminIdStr);

        // Envoyer un email au client
        sendRejectionEmail(refundRequest);

        return toDTO(refundRequest);
    }

    /**
     * Marque une demande comme remboursée.
     */
    @Transactional
    public RefundRequestDTO markAsRefunded(String refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Demande de remboursement non trouvée"));

        if (refundRequest.getStatus() != RefundStatus.APPROVED) {
            throw new IllegalArgumentException("Seules les demandes approuvées peuvent être marquées comme remboursées");
        }

        refundRequest.setStatus(RefundStatus.REFUNDED);
        refundRequest = refundRequestRepository.save(refundRequest);

        log.info("Demande de remboursement marquée comme remboursée: {}", refundRequestId);

        // Envoyer un email au client
        sendRefundConfirmationEmail(refundRequest);

        return toDTO(refundRequest);
    }

    /**
     * Sauvegarde un fichier justificatif.
     */
    private String saveFile(MultipartFile file, String appointmentId) throws IOException {
        // Créer le dossier s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String fileName = appointmentId + "_" + UUID.randomUUID() + extension;

        // Sauvegarder le fichier
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    /**
     * Envoie une notification à l'admin.
     */
    private void sendAdminNotification(RefundRequest refundRequest) {
        try {
            String subject = "Nouvelle demande de remboursement";
            LocalDate appointmentDate = refundRequest.getAppointment().getAppointmentDate();
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, refundRequest.getAppointment().getStartTime());

            String body = String.format(
                    "Une nouvelle demande de remboursement a été soumise.\n\n" +
                            "Client: %s %s\n" +
                            "Email: %s\n" +
                            "Rendez-vous: %s\n" +
                            "Service: %s\n" +
                            "Montant: %.2f€\n" +
                            "Raison: %s\n\n" +
                            "Veuillez traiter cette demande dans l'interface d'administration.",
                    refundRequest.getUser().getFirstName(),
                    refundRequest.getUser().getLastName(),
                    refundRequest.getUser().getEmail(),
                    appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    refundRequest.getAppointment().getService().getName(),
                    refundRequest.getAppointment().getService().getPriceCents() / 100.0,
                    refundRequest.getReason()
            );

            // TODO: Récupérer l'email admin depuis la configuration
            emailService.sendSimpleEmail("coiffeurbelge@gmail.com", subject, body);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à l'admin", e);
        }
    }

    /**
     * Envoie un email d'approbation au client.
     */
    private void sendApprovalEmail(RefundRequest refundRequest) {
        try {
            String subject = "Demande de remboursement approuvée";
            LocalDate appointmentDate = refundRequest.getAppointment().getAppointmentDate();
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, refundRequest.getAppointment().getStartTime());

            String body = String.format(
                    "Bonjour %s,\n\n" +
                            "Votre demande de remboursement pour le rendez-vous du %s a été approuvée.\n\n" +
                            "Montant: %.2f€\n" +
                            "Commentaire: %s\n\n" +
                            "Le remboursement sera effectué sous 5 à 7 jours ouvrés.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe du salon",
                    refundRequest.getUser().getFirstName(),
                    appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    refundRequest.getAppointment().getService().getPriceCents() / 100.0,
                    refundRequest.getAdminComment() != null ? refundRequest.getAdminComment() : "Aucun commentaire"
            );

            emailService.sendSimpleEmail(refundRequest.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email d'approbation", e);
        }
    }

    /**
     * Envoie un email de rejet au client.
     */
    private void sendRejectionEmail(RefundRequest refundRequest) {
        try {
            String subject = "Demande de remboursement rejetée";
            LocalDate appointmentDate = refundRequest.getAppointment().getAppointmentDate();
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, refundRequest.getAppointment().getStartTime());

            String body = String.format(
                    "Bonjour %s,\n\n" +
                            "Votre demande de remboursement pour le rendez-vous du %s a été rejetée.\n\n" +
                            "Raison: %s\n\n" +
                            "Si vous avez des questions, n'hésitez pas à nous contacter.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe du salon",
                    refundRequest.getUser().getFirstName(),
                    appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    refundRequest.getAdminComment() != null ? refundRequest.getAdminComment() : "Aucune raison fournie"
            );

            emailService.sendSimpleEmail(refundRequest.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de rejet", e);
        }
    }

    /**
     * Envoie un email de confirmation de remboursement.
     */
    private void sendRefundConfirmationEmail(RefundRequest refundRequest) {
        try {
            String subject = "Remboursement effectué";
            LocalDate appointmentDate = refundRequest.getAppointment().getAppointmentDate();
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, refundRequest.getAppointment().getStartTime());

            String body = String.format(
                    "Bonjour %s,\n\n" +
                            "Le remboursement de %.2f€ pour votre rendez-vous du %s a été effectué.\n\n" +
                            "Vous devriez recevoir le montant sur votre compte sous 2 à 3 jours ouvrés.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe du salon",
                    refundRequest.getUser().getFirstName(),
                    refundRequest.getAppointment().getService().getPriceCents() / 100.0,
                    appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            emailService.sendSimpleEmail(refundRequest.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de confirmation de remboursement", e);
        }
    }

    /**
     * Vérifie si un utilisateur peut accéder à un fichier justificatif.
     * Un utilisateur peut accéder uniquement à ses propres fichiers.
     */
    public boolean canUserAccessFile(String userEmail, String fileName) {
        // Trouver l'utilisateur par email
        User user = userRepository.findByEmail(userEmail)
                .orElse(null);

        if (user == null) {
            return false;
        }

        // Chercher une demande de remboursement avec ce nom de fichier et cet utilisateur
        List<RefundRequest> userRequests = refundRequestRepository.findByUserId(user.getId());

        return userRequests.stream()
                .anyMatch(request -> fileName.equals(request.getJustificationFileName()));
    }

    /**
     * Convertit une entité RefundRequest en DTO.
     */
    private RefundRequestDTO toDTO(RefundRequest refundRequest) {
        LocalDate appointmentDate = refundRequest.getAppointment().getAppointmentDate();
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, refundRequest.getAppointment().getStartTime());

        return RefundRequestDTO.builder()
                .id(refundRequest.getId())
                .appointmentId(refundRequest.getAppointment().getId().toString())
                .userId(refundRequest.getUser().getId().toString())
                .userName(refundRequest.getUser().getFirstName() + " " + refundRequest.getUser().getLastName())
                .userEmail(refundRequest.getUser().getEmail())
                .appointmentDate(appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .serviceName(refundRequest.getAppointment().getService().getName())
                .amount(refundRequest.getAppointment().getService().getPriceCents() / 100.0)
                .reason(refundRequest.getReason())
                .justificationFileName(refundRequest.getJustificationFileName())
                .justificationFileUrl(refundRequest.getJustificationFilePath() != null
                        ? "/api/v1/refunds/files/" + refundRequest.getJustificationFileName()
                        : null)
                .status(refundRequest.getStatus())
                .adminComment(refundRequest.getAdminComment())
                .processedBy(refundRequest.getProcessedBy() != null
                        ? refundRequest.getProcessedBy().getFirstName() + " " + refundRequest.getProcessedBy().getLastName()
                        : null)
                .processedAt(refundRequest.getProcessedAt())
                .createdAt(refundRequest.getCreatedAt())
                .updatedAt(refundRequest.getUpdatedAt())
                .build();
    }
}
