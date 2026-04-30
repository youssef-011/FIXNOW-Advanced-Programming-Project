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
            seedUser(userRepo, passwordEncoder, "Ahmed Plumber", "tech.plumber@fixnow.com", "tech123", "+201000000102", Role.TECHNICIAN);
            seedUser(userRepo, passwordEncoder, "Omar Electrician", "tech.electric@fixnow.com", "tech123", "+201000000103", Role.TECHNICIAN);

            seedTechnician(technicianRepo, "Ahmed Plumber", "Plumbing");
            seedTechnician(technicianRepo, "Omar Electrician", "Electricity");
        };
    }

    private void seedUser(UserRepo userRepo, PasswordEncoder passwordEncoder, String name, String email, String password, String phone, Role role) {
        if (userRepo.findByEmail(email).isPresent()) {
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);

        userRepo.save(user);
    }

    private void seedTechnician(TechnicianRepo technicianRepo, String name, String skill) {
        boolean exists = technicianRepo.findAll().stream()
                .anyMatch(technician -> matches(technician.getName(), name) && matches(technician.getSkill(), skill));

        if (exists) {
            return;
        }

        Technician technician = new Technician();
        technician.setName(name);
        technician.setSkill(skill);
        technician.setAvailable(true);
        technician.setRating(0.0);

        technicianRepo.save(technician);
    }

    private boolean matches(String currentValue, String expectedValue) {
        return currentValue != null && currentValue.equalsIgnoreCase(expectedValue);
    }
}
