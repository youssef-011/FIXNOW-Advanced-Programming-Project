package com.fix.fixnow.config;

import com.fix.fixnow.model.Role;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDemoData(UserRepo userRepo, TechnicianRepo technicianRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            seedUser(userRepo, passwordEncoder, "Admin User", "admin@fixnow.com", "admin123", "+201000000100", Role.ADMIN);
            seedUser(userRepo, passwordEncoder, "Customer User", "customer@fixnow.com", "customer123", "+201000000101", Role.CUSTOMER);
            User plumberUser = seedUser(userRepo, passwordEncoder, "Ahmed Plumber", "tech.plumber@fixnow.com", "tech123", "+201000000102", Role.TECHNICIAN);
            User electricianUser = seedUser(userRepo, passwordEncoder, "Omar Electrician", "tech.electric@fixnow.com", "tech123", "+201000000103", Role.TECHNICIAN);

            seedTechnician(technicianRepo, plumberUser, "Ahmed Plumber", "Plumbing");
            seedTechnician(technicianRepo, electricianUser, "Omar Electrician", "Electricity");
        };
    }

    private User seedUser(UserRepo userRepo, PasswordEncoder passwordEncoder, String name, String email, String password, String phone, Role role) {
        return userRepo.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone(phone);
            user.setRole(role);

            return userRepo.save(user);
        });
    }

    private void seedTechnician(TechnicianRepo technicianRepo, User user, String name, String skill) {
        if (technicianRepo.findByUser_Id(user.getId()).isPresent()) {
            return;
        }

        Technician technician = technicianRepo.findAll().stream()
                .filter(existingTechnician -> matches(existingTechnician.getName(), name) && matches(existingTechnician.getSkill(), skill))
                .findFirst()
                .orElse(null);

        if (technician != null) {
            technician.setUser(user);
            technicianRepo.save(technician);
            return;
        }

        Technician newTechnician = new Technician();
        newTechnician.setName(name);
        newTechnician.setSkill(skill);
        newTechnician.setAvailable(true);
        newTechnician.setRating(0.0);
        newTechnician.setUser(user);

        technicianRepo.save(newTechnician);
    }

    private boolean matches(String currentValue, String expectedValue) {
        return currentValue != null && currentValue.equalsIgnoreCase(expectedValue);
    }
}
