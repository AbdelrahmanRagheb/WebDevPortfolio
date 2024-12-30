package org.abdelrahman.ecommerce.controller;

import org.abdelrahman.ecommerce.entity.Product;
import org.abdelrahman.ecommerce.entity.User;
import org.abdelrahman.ecommerce.model.HomePageModel;
import org.abdelrahman.ecommerce.model.HomePageView;
import org.abdelrahman.ecommerce.model.ProductView;
import org.abdelrahman.ecommerce.service.AppService;
import org.abdelrahman.ecommerce.service.ProductUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private AppService appService;
    private ProductUtilityService productUtilityService;
    @Value("${product.categories}")
    private List<String> categories;

    @Autowired
    HomeController(AppService appService, ProductUtilityService productUtilityService) {
        this.appService = appService;
        this.productUtilityService = productUtilityService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        HomePageModel homePageModel = appService.fetchHomePage(100);
        HomePageView homePageView;

        boolean isAuthenticated=false;
        if (principal!=null) {
            isAuthenticated=true;
            User user = appService.getUserByUserName(principal.getName());
            homePageView = productUtilityService.mapProductsToProductsView(user, homePageModel);
        }else homePageView = productUtilityService.mapProductsToProductsView(null, homePageModel);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("categories", categories);
        homePageView.setHotTrendingCategories(homePageModel.getHotTrendingCategories());
        /**/
        model.addAttribute("topRatedProducts", homePageView.getTopRatedProducts());
        model.addAttribute("productUnderSpecificValue", homePageView.getProductsUnderSpecificValue());
        model.addAttribute("trendingCategories", homePageView.getHotTrendingCategories());
        System.out.println("==========:::++++++++"+homePageView.getHotTrendingCategories());
        /**/
        return "home-page";
    }

    @GetMapping("/{category}")
    public String getProductsByCategory(@PathVariable("category") String category, Model model, Principal principal) {
        // Get products filtered by the selected category
        System.out.println("/{category}" + category);
        List<Product> productsByCategory = appService.getProductsByCategory(category);

        List<ProductView> searchRes = productsByCategory.stream()
                .map(ProductView::new)
                .toList();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            User user = appService.getUserByUserName(principal.getName());
            searchRes = productUtilityService.mapProductsToProductsViewHelper(user, productsByCategory);
        }
        List<Map.Entry<String, Integer>> brands = productUtilityService.filterOnBrands(searchRes);
        List<Map.Entry<String, Integer>> ratings = productUtilityService.filterOnRating(searchRes);

        model.addAttribute("isAuthenticated", isAuthenticated);

        // Add the results and query to the model to display on the page
        model.addAttribute("products", searchRes);
        model.addAttribute("brands", brands);
        model.addAttribute("ratings", ratings);

        return "products-page";  // Your view for category products
    }

}
