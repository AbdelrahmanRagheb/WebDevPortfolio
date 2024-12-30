package org.abdelrahman.ecommerce.service;

import jakarta.persistence.TypedQuery;
import org.abdelrahman.ecommerce.dao.*;
import org.abdelrahman.ecommerce.entity.*;
import org.abdelrahman.ecommerce.model.HomePageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppService {

    private ProductDAO productDAO;

    private UserDAO userDAO;
    private WishListDAO wishListDAO;
    private OrdersDAO ordersDAO;

    @Autowired
    AppService(ProductDAO productDAO, UserDAO userDAO, WishListDAO wishListDAO, OrdersDAO ordersDAO) {
        this.productDAO = productDAO;
        this.userDAO = userDAO;
        this.wishListDAO = wishListDAO;
        this.ordersDAO = ordersDAO;

    }


    public void addReview(Long productId, Review review) {
        Product product = productDAO.getProduct(productId);
        product.addReview(review);
        //reviewDAO.updateReview(review);
        productDAO.updateProduct(product);
    }

    public List<Review> getReviewsOnProduct(Long productId) {
        return productDAO.getReviewsOnProduct(productId);
    }

    public void addProduct(Product product) {
        productDAO.addProduct(product);
    }

    public void updateProduct(Product product) {
        productDAO.updateProduct(product);
    }

    public void deleteProduct(Product product) {
        productDAO.deleteProduct(product);
    }

    public Product getProduct(Long id) {
        return productDAO.getProduct(id);
    }

    public List<Product> getNProduct(int n) {
        return productDAO.getNProduct(n);
    }


    public List<Product> getProductsByCategory(String category) {
        return productDAO.getProductsByCategory(category);
    }

    public List<Product> getProductsByBrand(String brand) {
        return productDAO.getProductsByBrand(brand);
    }

    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productDAO.getProductsByCategoryAndBrand(category, brand);
    }

    public void addNewUser(User user) {
        userDAO.addNewUser(user);
    }

    @Transactional
    public void updateUserInfo(User user) {
        System.out.println("++++++++++++++++++++++++++++::method call::+++++++++++++++++++++++++++");
        System.out.println("updateUserInfo: called" + user.getId());

        userDAO.updateUserInfo(user);
    }

    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }


    public User getUserByUserName(String username) {
        return userDAO.getUserByUserName(username);
    }

    public List<User> getUsersList() {
        return userDAO.getUsersList();
    }

    public void addToWishList(WishList wishList) {
        wishListDAO.addToWishList(wishList);
    }

    public void removeFromWishList(WishList wishList) {
        wishListDAO.removeFromWishList(wishList);
    }


    public List<WishList> getWishList() {
        return wishListDAO.getWishList();
    }


    public List<WishList> getWishListByCustomerId(int customerId) {
        return wishListDAO.getWishListByCustomerId(customerId);
    }


    public WishList getProductFromWishList(Long productId) {
        return wishListDAO.getProductFromWishList(productId);
    }

    public void addOrder(Orders orders) {
        ordersDAO.addOrder(orders);
    }

    public void deleteOrder(Orders order) {
        ordersDAO.deleteOrder(order);
    }

    public List<Orders> getOrders() {
        return ordersDAO.getOrders();
    }

    public Orders getOrder(Long id) {
        return ordersDAO.getOrder(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productDAO.searchProducts(keyword);
    }


    public HomePageModel fetchHomePage(double price) {
        return new HomePageModel(productDAO.getTopRatedProducts(10), productDAO.getProductsUnderPrice(10,price), productDAO.getTopCategories());
    }

    public List<Product> getTopRatedProducts() {
        return productDAO.getTopRatedProducts();
    }

    public List<Product> getProductsUnderPrice(double price) {
      return   productDAO.getProductsUnderPrice(price);
    }
}
