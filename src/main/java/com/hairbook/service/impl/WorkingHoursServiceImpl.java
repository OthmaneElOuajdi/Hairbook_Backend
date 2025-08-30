package com.hairbook.service.impl;

import com.hairbook.entity.WorkingHours;
import com.hairbook.repository.WorkingHoursRepository;
import com.hairbook.service.WorkingHoursService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service pour la gestion des horaires de travail.
 */
@Service
@Transactional
public class WorkingHoursServiceImpl implements WorkingHoursService {

    private final WorkingHoursRepository repo;

    /**
     * Constructeur pour l'injection du repository des horaires de travail.
     *
     * @param repo Le repository pour les entités WorkingHours.
     */
    public WorkingHoursServiceImpl(WorkingHoursRepository repo) {
        this.repo = repo;
    }

    @Override
    public WorkingHours save(WorkingHours wh) {
        return repo.save(wh);
    }

    @Override
    public Optional<WorkingHours> findByDay(DayOfWeek day) {
        return repo.findByDayOfWeek(day);
    }

    @Override
    public List<WorkingHours> findAll() {
        return repo.findAll();
    }
}
