package com.fix.fixnow.dto;

public class RequestTimelineStep {

    private final String status;
    private final String label;
    private final String description;
    private final boolean active;
    private final boolean complete;

    public RequestTimelineStep(String status, String label, String description, boolean active, boolean complete) {
        this.status = status;
        this.label = label;
        this.description = description;
        this.active = active;
        this.complete = complete;
    }

    public String getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isComplete() {
        return complete;
    }
}
