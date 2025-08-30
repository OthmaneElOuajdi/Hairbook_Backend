package com.hairbook.service.impl;

import com.hairbook.entity.BlockedSlot;
import com.hairbook.repository.BlockedSlotRepository;
import com.hairbook.service.BlockedSlotService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du service pour la gestion des créneaux horaires bloqués.
 */
@Service
@Transactional
public class BlockedSlotServiceImpl implements BlockedSlotService {

    private final BlockedSlotRepository repo;

    /**
     * Constructeur pour l'injection du repository des créneaux bloqués.
     *
     * @param repo Le repository pour les entités BlockedSlot.
     */
    public BlockedSlotServiceImpl(BlockedSlotRepository repo) {
        this.repo = repo;
    }

    @Override
    public BlockedSlot create(BlockedSlot slot) {
        return repo.save(slot);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<BlockedSlot> findBetween(LocalDateTime start, LocalDateTime end) {
        return repo.findByStartAtLessThanEqualAndEndAtGreaterThanEqual(end, start);
    }
}
