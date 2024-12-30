package org.abdelrahman.ecommerce.dao;

import org.abdelrahman.ecommerce.entity.WishList;

import java.util.List;

public interface WishListDAO {
    void addToWishList(WishList wishList);
    void removeFromWishList(WishList wishListDAO);
    List<WishList> getWishList();
    List<WishList> getWishListByCustomerId(int customerId);
    WishList getProductFromWishList(Long productId);

}
