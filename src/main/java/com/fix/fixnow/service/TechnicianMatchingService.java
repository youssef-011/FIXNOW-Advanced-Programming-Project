package com.fix.fixnow.service;

import com.fix.fixnow.model.Technician;
import com.fix.fixnow.repository.TechnicianRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class TechnicianMatchingService {

    private final TechnicianRepo technicianRepo;

    public TechnicianMatchingService(TechnicianRepo technicianRepo) {
        this.technicianRepo = technicianRepo;
    }

    public Optional<Technician> findBestAvailableMatch(String category) {
        return findAvailableMatches(category).stream().findFirst();
    }

    public List<Technician> findAvailableMatches(String category) {
        return technicianRepo.findByAvailable(true).stream()
                .filter(technician -> matchesCategory(technician, category))
                .toList();
    }

    public boolean matchesCategory(Technician technician, String category) {
        if (technician == null) {
            return false;
        }
        return canonicalSkill(technician.getSkill()).equals(canonicalSkill(category));
    }

    public String canonicalSkill(String value) {
        String normalized = normalize(value);
        if (normalized.contains("plumb")) {
            return "plumbing";
        }
        if (normalized.contains("electric")) {
            return "electricity";
        }
        if (normalized.equals("ac") || normalized.contains("ac repair") || normalized.contains("air condition")) {
            return "ac repair";
        }
        if (normalized.contains("lock")) {
            return "locksmith";
        }
        if (normalized.contains("appliance") || normalized.contains("fridge") || normalized.contains("washer")) {
            return "appliance repair";
        }
        if (normalized.contains("general")) {
            return "general maintenance";
        }
        return normalized;
    }

    private String normalize(String value) {
        return String.valueOf(value == null ? "" : value)
                .replace('_', ' ')
                .replace('-', ' ')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
