package com.hairbook.service.impl;

import com.hairbook.entity.ERole;
import com.hairbook.entity.Role;
import com.hairbook.repository.RoleRepository;
import com.hairbook.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service pour la gestion des rôles.
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * Constructeur pour l'injection du repository des rôles.
     *
     * @param roleRepository Le repository pour les entités Role.
     */
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByName(ERole name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }
}
