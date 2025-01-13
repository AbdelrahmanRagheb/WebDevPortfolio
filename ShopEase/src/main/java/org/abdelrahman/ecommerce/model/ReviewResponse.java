package org.abdelrahman.ecommerce.model;

public class ReviewResponse {
    private String message;

    public ReviewResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}