package org.abdelrahman.ecommerce.entity;

import jakarta.persistence.*;
import org.springframework.stereotype.Indexed;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Indexed
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "`desc`")  // Use backticks around the column name
    private String desc;

    @Column(name ="brand")
    private String brand;


    @Column(name = "price")
    private double price;

    @Column(name = "category")
    private String category;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH,CascadeType.DETACH})
    @JoinColumn(name = "vendor_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "product_like",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> usersLikeThisProduct;


    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy = "product")
    private List<Review> reviews;

    @Column(name = "rating")
    private double rating = 0.0;

    @Column(name = "rating_count")
    private int ratingCount = 0;



    public Product() {
    }

    public Product(String title, String desc, double price, String category,String brand) {
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.category = category;
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }

    public List<User> getUsersLikeThisProduct() {
        return usersLikeThisProduct;
    }

    public void setUsersLikeThisProduct(List<User> usersLikeThisProduct) {
        this.usersLikeThisProduct = usersLikeThisProduct;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void  addReview(Review review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        review.setProduct(this);
        reviews.add(review);
        this.ratingCount++;
        this.rating+=review.getRating();
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
