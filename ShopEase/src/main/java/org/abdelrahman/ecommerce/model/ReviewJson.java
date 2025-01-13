package org.abdelrahman.ecommerce.model;

public class ReviewJson {
    private String comment;
    private int rating;
    private Long productId;
    private String username;
    private String createdAt ;

    public ReviewJson() {

    }

    public ReviewJson(String comment, int rating, Long productId, String username,String createdAt) {
        this.comment = comment;
        this.rating = rating;
        this.productId = productId;
        this.username = username;
        this.createdAt = createdAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "ReviewJson{" +
                "comment='" + comment + '\'' +
                ", rating=" + rating +
                ", productId=" + productId +
                ", username='" + username + '\'' +
                '}';
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
