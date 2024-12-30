package org.abdelrahman.ecommerce.controller;

import org.abdelrahman.ecommerce.entity.Orders;
import org.abdelrahman.ecommerce.entity.Product;
import org.abdelrahman.ecommerce.entity.User;
import org.abdelrahman.ecommerce.model.ProductView;
import org.abdelrahman.ecommerce.service.AppService;
import org.abdelrahman.ecommerce.service.ProductUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private final AppService appService;
    private ProductUtilityService productUtilityService;

    @Autowired
    DashboardController(AppService appService, ProductUtilityService productUtilityService) {
        this.appService = appService;
        this.productUtilityService = productUtilityService;
    }


    @GetMapping("/dashboard")
    public String showProfile(Model model, Principal principal) {
        User user = appService.getUserByUserName(principal.getName());


        List<Product> products = new ArrayList<>();
        user.getWishlist().forEach(w -> {
            products.add(w.getProduct());
        });

        List<ProductView> productStatuses = productUtilityService.mapProductsToProductsViewHelper(user, products);
        List<Orders> orders = user.getOrders();

        model.addAttribute("userInfo", user);
        model.addAttribute("wishlist", productStatuses);
        orders.forEach(o -> {
            System.out.println("order id: " + o.getId() + " product title: " + o.getProduct().getTitle());
        });
        model.addAttribute("orders", orders);

        System.out.println("======================================," + user);
        return "dashboard-page";
    }

    @GetMapping("/updateUserInfo")
    public String updateUserInfo(User user) {
        return "dashboard-page";
    }

    @PostMapping("/createNewUser")
    public String createNewUser(User user) {
        System.out.println("================::::===================" + user.getEmail());
        appService.addNewUser(new User(user.getFullName(), user.getUsername(), user.getPassword(), user.getEmail(), user.getAddress(), user.getAccountType()));
        return "home-page";
    }
}
