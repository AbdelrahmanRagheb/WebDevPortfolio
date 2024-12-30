package org.abdelrahman.ecommerce.service;

import org.abdelrahman.ecommerce.entity.Product;
import org.abdelrahman.ecommerce.entity.User;
import org.abdelrahman.ecommerce.model.HomePageModel;
import org.abdelrahman.ecommerce.model.HomePageView;
import org.abdelrahman.ecommerce.model.ProductView;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ProductUtilityService {

    public HomePageView mapProductsToProductsView(User user, HomePageModel homePageModel) {
        HomePageView homePageView = new HomePageView();
        homePageView.setTopRatedProducts(mapProductsToProductsViewHelper(user,homePageModel.getTopRatedProducts()));
        homePageView.setProductsUnderSpecificValue(mapProductsToProductsViewHelper(user,homePageModel.getProductsUnderSpecificValue()));
        return homePageView;
    }

    public List<ProductView> mapProductsToProductsViewHelper(User user, List<Product> products) {
        List<ProductView> productViews = products.stream()
                .map(ProductView::new)
                .toList();
        if (user==null) return productViews;
        List<Long> productWishListed = new ArrayList<>();
        user.getWishlist().forEach(e -> productWishListed.add(e.getProduct().getId()));

        if (!user.getLikes().isEmpty()) {
            productViews.forEach(p -> {
                int i = user.getLikes().indexOf(p.getProduct());
                if (i != -1) {
                    p.setLiked(true);
                }
                int n = productWishListed.indexOf(p.getProduct().getId());
                if (n != -1) {
                    p.setWishListed(true);
                }
            });
        }
        return productViews;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public List<Map.Entry<String, Integer>> filterOnBrands(List<ProductView> productSearchRes) {
        Map<String, Integer> brands = new HashMap<>();
        brands.put("ALL", productSearchRes.size());
        productSearchRes.forEach(p -> {
            String brandName = p.getProduct().getBrand().toUpperCase();
            p.setBrand(brandName);
            if (brands.containsKey(brandName)) {
                brands.compute(brandName, (k, i) -> i + 1);
            } else brands.put(brandName, 1);

        });
        List<Map.Entry<String, Integer>> sortedBrands = new ArrayList<>(brands.entrySet());
        sortedBrands.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedBrands;
    }

    public List<Map.Entry<String, Integer>> filterOnRating(List<ProductView> searchRes) {
        Map<String, Integer> ratings = new HashMap<>();
        ratings.put("0.0", searchRes.size());
        ratings.put("4.0", 0);
        ratings.put("3.0", 0);
        ratings.put("2.0", 0);
        ratings.put("1.0", 0);
        searchRes.forEach(p -> {
            Product product = p.getProduct();
            double r = product.getRating() / (double) product.getRatingCount();
            p.setRating(r);
            if (r >= 4.0) {
                ratings.put("4.0", ratings.get("4.0") + 1);
                ratings.put("3.0", ratings.get("3.0") + 1);
                ratings.put("2.0", ratings.get("2.0") + 1);
                ratings.put("1.0", ratings.get("1.0") + 1);
            } else if (r >= 3.0) {
                ratings.put("3.0", ratings.get("3.0") + 1);
                ratings.put("2.0", ratings.get("2.0") + 1);
                ratings.put("1.0", ratings.get("1.0") + 1);
            } else if (r >= 2.0) {

                ratings.put("2.0", ratings.get("2.0") + 1);
                ratings.put("1.0", ratings.get("1.0") + 1);
            } else if (r >= 1.0) {

                ratings.put("1.0", ratings.get("1.0") + 1);
            }

        });
        List<Map.Entry<String, Integer>> productSortedByRating = new ArrayList<>(ratings.entrySet());
        productSortedByRating.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        productSortedByRating.forEach(p -> {
            System.out.println(p.getKey() + " : " + p.getValue());
        });
        return productSortedByRating;
    }


}
