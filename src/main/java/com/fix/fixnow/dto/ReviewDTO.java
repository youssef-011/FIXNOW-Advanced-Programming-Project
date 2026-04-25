package com.fix.fixnow.dto;

public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long userId;
    private Long technicianId;

    public ReviewDTO() {

    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getTechnicianId() {
        return technicianId;
    }
    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
}
