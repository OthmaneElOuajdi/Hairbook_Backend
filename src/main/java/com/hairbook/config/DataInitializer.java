package com.hairbook.config;

import com.hairbook.entity.ERole;
import com.hairbook.entity.Role;
import com.hairbook.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialise les données de base nécessaires au fonctionnement de l'application
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    /**
     * Crée les rôles de base s'ils n'existent pas
     */
    private void initializeRoles() {
        if (roleRepository.findByName(ERole.ROLE_MEMBER).isEmpty()) {
            Role memberRole = new Role();
            memberRole.setName(ERole.ROLE_MEMBER);
            roleRepository.save(memberRole);
            System.out.println("Rôle ROLE_MEMBER créé automatiquement");
        }

        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            System.out.println("Rôle ROLE_ADMIN créé automatiquement");
        }
    }
}
