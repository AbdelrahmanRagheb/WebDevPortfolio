package org.abdelrahman.ecommerce.model;

import org.abdelrahman.ecommerce.entity.Product;

public class ProductView {
    private Product product;
    private boolean isLiked=false;
    private boolean isWishListed=false;
    private String brand = "All";
    private double rating=0.0;

    public ProductView() {
    }

    public ProductView(Product product, boolean isLiked, boolean isWishListed, String brand) {
        this.product = product;
        this.isLiked = isLiked;
        this.isWishListed = isWishListed;
        this.brand = brand;

        this.rating = product.getRating()/(double) product.getRatingCount();
    }

    public ProductView(Product product) {
        this.product = product;
        this.rating = product.getRating()/(double) product.getRatingCount();
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.rating = product.getRating()/(double) product.getRatingCount();
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isWishListed() {
        return isWishListed;
    }

    public void setWishListed(boolean wishListed) {
        isWishListed = wishListed;
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
}
