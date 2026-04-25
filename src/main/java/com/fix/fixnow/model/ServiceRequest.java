package com.fix.fixnow.model;

import jakarta.persistence.*;

@Entity
@Table(name="service_requests")
//ServiceRequest = el order bta3 el service
public class ServiceRequest
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description; // description bta3 el problem
    private String status;// PENDING / IN_PROGRESS / DONE

    @ManyToOne
    @JoinColumn(name ="user_id")
    // kaza request le user wa7ed
    private User user;

    @ManyToOne
    @JoinColumn(name="technician_id")
    // kaza request le tech wa7ed
    private Technician technician;

    public ServiceRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

}
