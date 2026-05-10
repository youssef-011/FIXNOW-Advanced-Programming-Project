package com.fix.fixnow.service;

import com.fix.fixnow.dto.RequestTimelineStep;
import com.fix.fixnow.model.ServiceRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestTimelineService {

    public List<RequestTimelineStep> buildTimeline(ServiceRequest request) {
        String currentStatus = request == null ? ServiceRequest.PENDING : request.getStatus();
        int currentIndex = statusIndex(currentStatus);

        return List.of(
                step(ServiceRequest.PENDING, "Request submitted", "The customer request is saved and waiting for dispatch.", currentIndex, 0),
                step(ServiceRequest.ASSIGNED, "Technician assigned", assignedDescription(request), currentIndex, 1),
                step(ServiceRequest.ACCEPTED, "Technician accepted", "The technician confirmed the job and is working on it.", currentIndex, 2),
                step(ServiceRequest.COMPLETED, "Job completed", "The service is done and ready for customer review.", currentIndex, 3)
        );
    }

    public String nextStepTitle(ServiceRequest request) {
        if (request == null) {
            return "Create request";
        }
        return switch (request.getStatus()) {
            case ServiceRequest.PENDING -> "Waiting for assignment";
            case ServiceRequest.ASSIGNED -> "Waiting for technician acceptance";
            case ServiceRequest.ACCEPTED -> "Technician is working";
            case ServiceRequest.COMPLETED -> "Ready for review";
            default -> "Track request";
        };
    }

    public String nextStepDescription(ServiceRequest request) {
        if (request == null) {
            return "Create a request first, then this page can display status and assignment data.";
        }
        return switch (request.getStatus()) {
            case ServiceRequest.PENDING -> "FixNow will match this request with an available technician who has the right skill.";
            case ServiceRequest.ASSIGNED -> "The assigned technician needs to accept the job before work starts.";
            case ServiceRequest.ACCEPTED -> "The technician can mark this job complete after finishing the service.";
            case ServiceRequest.COMPLETED -> "The customer can now add one review for this completed request.";
            default -> "Follow the request status here.";
        };
    }

    private RequestTimelineStep step(String status, String label, String description, int currentIndex, int stepIndex) {
        return new RequestTimelineStep(status, label, description, currentIndex == stepIndex, currentIndex >= stepIndex);
    }

    private String assignedDescription(ServiceRequest request) {
        if (request != null && request.getTechnician() != null) {
            return "Matched with " + request.getTechnician().getName() + " for this service category.";
        }
        return "No matching available technician yet; admin can assign one later.";
    }

    private int statusIndex(String status) {
        return switch (String.valueOf(status)) {
            case ServiceRequest.ASSIGNED -> 1;
            case ServiceRequest.ACCEPTED -> 2;
            case ServiceRequest.COMPLETED -> 3;
            case ServiceRequest.PENDING -> 0;
            default -> 0;
        };
    }
}
