package com.fix.fixnow.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "technicians")
//Technician = b yshtghl or b enfz requests w yakhod ratings
public class Technician {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id; // primary key
    private  String name;
    private  String skill; // zay plumbing / electric

    private  double rating;
    private boolean available = true;
    @OneToMany(mappedBy = "technician")    // tech wa7ed yshof kaza request
    private List<ServiceRequest> requests;

    @OneToMany(mappedBy = "technician")    // tech leeh kaza review
    private List<Review> reviews;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<ServiceRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<ServiceRequest> requests) {
        this.requests = requests;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Technician(){

}


}
