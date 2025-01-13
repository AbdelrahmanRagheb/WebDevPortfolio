package org.abdelrahman.ecommerce.model;

import org.abdelrahman.ecommerce.entity.Product;

public class ProductLikeAndWishListStatus {

    private Product product;
    private boolean isLiked=false;
    private boolean isWishListed=false;
    private String brand = "OTHER";

    public ProductLikeAndWishListStatus() {
    }

    public ProductLikeAndWishListStatus(Product product, boolean isLiked, boolean isWishListed,String brand) {
        this.product = product;
        this.isLiked = isLiked;
        this.isWishListed = isWishListed;
        this.brand = brand;
    }
   public ProductLikeAndWishListStatus(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public String toString() {
        return "ProductWithLike{" +
                "product=" + product +
                ", isLiked=" + isLiked +
                '}';
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
}
