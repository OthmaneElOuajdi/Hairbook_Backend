package com.hairbook.service.impl;

import com.hairbook.entity.ServiceItem;
import com.hairbook.repository.ServiceItemRepository;
import com.hairbook.service.ServiceItemService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service pour la gestion des prestations (services) offertes
 * par le salon.
 */
@Service
@Transactional
public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemRepository repo;

    /**
     * Constructeur pour l'injection du repository des prestations.
     *
     * @param repo Le repository pour les entités ServiceItem.
     */
    public ServiceItemServiceImpl(ServiceItemRepository repo) {
        this.repo = repo;
    }

    @Override
    public ServiceItem create(ServiceItem s) {
        return repo.save(s);
    }

    @Override
    public Optional<ServiceItem> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<ServiceItem> findAll() {
        return repo.findAll();
    }

    @Override
    public ServiceItem update(ServiceItem s) {
        return repo.save(s);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
