package org.abdelrahman.ecommerce.model;

public class ProductLikeState {
    private boolean liked;
    private Long productId;

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
