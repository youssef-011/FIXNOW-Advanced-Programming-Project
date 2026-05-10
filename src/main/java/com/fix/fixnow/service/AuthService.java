package com.fix.fixnow.service;

import com.fix.fixnow.exception.BadRequestException;
import com.fix.fixnow.model.Role;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private static final String DEFAULT_TECHNICIAN_SKILL = "General Maintenance";

    private final UserRepo userRepo;
    private final TechnicianRepo technicianRepo;
    private final PasswordEncoder passwordEncoder; //encryption password

    public AuthService(UserRepo userRepo, TechnicianRepo technicianRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.technicianRepo = technicianRepo;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public User register(User user) {
        return register(user, DEFAULT_TECHNICIAN_SKILL, null);
    }

    @Transactional
    public User register(User user, String technicianSkill) {
        return register(user, technicianSkill, null);
    }

    @Transactional
    public User register(User user, String technicianSkill, String technicianDescription) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {     //find el email mawgod wala la
            throw new BadRequestException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);
        ensureTechnicianProfile(savedUser, technicianSkill, technicianDescription);
        return savedUser;
    }


    public Optional<User> login(String email, String password) {
        return userRepo.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    private void ensureTechnicianProfile(User user, String technicianSkill, String technicianDescription) {
        if (!Role.TECHNICIAN.equals(user.getRole())) {
            return;
        }

        if (technicianRepo.findByUser_Id(user.getId()).isPresent()) {
            return;
        }

        Technician technician = new Technician();
        technician.setName(user.getName());
        technician.setSkill(normalizeSkill(technicianSkill));
        technician.setDescription(normalizeDescription(technicianDescription));
        technician.setAvailable(true);
        technician.setRating(0.0);
        technician.setUser(user);
        technicianRepo.save(technician);
    }

    private String normalizeSkill(String technicianSkill) {
        if (technicianSkill == null || technicianSkill.trim().isEmpty()) {
            return DEFAULT_TECHNICIAN_SKILL;
        }
        return technicianSkill.trim();
    }

    private String normalizeDescription(String technicianDescription) {
        if (technicianDescription == null || technicianDescription.trim().isEmpty()) {
            return null;
        }
        return technicianDescription.trim();
    }
}
