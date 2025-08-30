package com.hairbook.service.impl;

import com.hairbook.entity.Payment;
import com.hairbook.entity.PaymentStatus;
import com.hairbook.repository.PaymentRepository;
import com.hairbook.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service pour la gestion des paiements.
 * Gère la création, la recherche et la mise à jour des paiements associés aux
 * réservations.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repo;

    /**
     * Constructeur pour l'injection du repository des paiements.
     *
     * @param repo Le repository pour les entités Payment.
     */
    public PaymentServiceImpl(PaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    public Payment create(Payment p) {
        return repo.save(p);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return repo.findById(id);
    }

    public Optional<Payment> findByReservation(Long reservationId) {
        return repo.findByReservationId(reservationId);
    }

    /**
     * Met à jour le statut et le message d'un paiement existant.
     *
     * @param paymentId L'ID du paiement à mettre à jour.
     * @param status    Le nouveau statut du paiement.
     * @param message   Un message descriptif (par exemple, un ID de transaction ou
     *                  une erreur).
     * @return Le paiement mis à jour.
     * @throws IllegalArgumentException si le paiement n'est pas trouvé.
     */
    @Override
    public Payment updateStatus(Long paymentId, PaymentStatus status, String message) {
        Payment p = repo.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        p.setStatus(status);
        p.setMessage(message);
        return repo.save(p);
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return repo.findByStatus(status);
    }
}
