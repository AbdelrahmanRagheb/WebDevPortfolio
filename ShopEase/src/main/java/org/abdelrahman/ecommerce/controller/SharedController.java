package org.abdelrahman.ecommerce.controller;

import org.abdelrahman.ecommerce.entity.Product;
import org.abdelrahman.ecommerce.entity.User;
import org.abdelrahman.ecommerce.entity.WishList;
import org.abdelrahman.ecommerce.model.ProductIdJson;
import org.abdelrahman.ecommerce.model.ProductLikeState;
import org.abdelrahman.ecommerce.model.ReviewResponse;
import org.abdelrahman.ecommerce.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class SharedController {
    private final AppService appService;

    @Autowired
    SharedController(AppService appService) {
        this.appService = appService;
    }

    @PostMapping("/product-reaction")
    @ResponseBody
    public void toggleLikeAndDislike(@RequestBody ProductLikeState productLikeState, Principal principal) {
        System.out.println("++++++++++++++++++++++++++++::START::+++++++++++++++++++++++++++");
        System.out.println("Received request to update product like status");
        System.out.println("Product ID: " + productLikeState.getProductId());
        System.out.println("Liked: " + productLikeState.isLiked());
        User user = appService.getUserByUserName(principal.getName());
        Product product = appService.getProduct(productLikeState.getProductId());
        if (user != null && product != null) {
            if (user.getLikes().contains(product)) {
                user.getLikes().remove(product);
                System.out.println("++++++++++++++++++++++++++++::exists::+++++++++++++++++++++++++++");
            } else {
                user.getLikes().add(product);
                System.out.println("++++++++++++++++++++++++++++::not exists::+++++++++++++++++++++++++++");
            }
            appService.updateUserInfo(user);
        }
        System.out.println("++++++++++++++++++++++++++++::END::+++++++++++++++++++++++++++");
    }



    @PostMapping("/add-to-wishlist")
    @ResponseBody
    public ReviewResponse addToWishList(@RequestBody ProductIdJson productId, Principal principal) {
        System.out.println("product add-to-wishlist==================================successfully" +productId.getProductId());
        User user = appService.getUserByUserName(principal.getName());
        Product product = appService.getProduct(productId.getProductId());
        System.out.println("username:" + user.getUsername());
        System.out.println("user id:" + user.getId());

        WishList existingWishlistItem = appService.getProductFromWishList(productId.getProductId());
        if (existingWishlistItem!=null) {
            System.out.println("Product already in wishlist, removing...");
            System.out.println("Product already in wishlist, "+existingWishlistItem);
            // user.getWishlist().remove(existingWishlistItem);
            user.removeProductFromWishList(existingWishlistItem);
            //appService.removeFromWishList(existingWishlistItem);
            System.out.println("done removing>>>>>>>>>>");
        } else {
            System.out.println("Adding product to wishlist...");
            WishList wishList = new WishList();
            wishList.setProduct(product);
            //user.getWishlist().add(wishList);
            System.out.println("Adding product to wishlist, "+wishList);
            user.addProductToWishList(wishList);
        }
        appService.updateUserInfo(user);
        return new ReviewResponse("succeeded");
    }
}
