package org.abdelrahman.ecommerce.model;

import org.abdelrahman.ecommerce.entity.Product;

import java.util.*;

public class HomePageModel {
    List<Product> topRatedProducts;
    List<Product> productsUnderSpecificValue;
    List<String> hotTrendingCategories;

    public HomePageModel() {
    }

    public HomePageModel(List<Product> topRatedProducts, List<Product> productsUnderSpecificValue, List<String> hotTrendingCategories) {
        this.topRatedProducts = topRatedProducts;
        this.productsUnderSpecificValue = productsUnderSpecificValue;
        this.hotTrendingCategories = hotTrendingCategories;
    }

    public List<Product> getTopRatedProducts() {
        return topRatedProducts;
    }

    public void setTopRatedProducts(List<Product> topRatedProducts) {
        this.topRatedProducts = topRatedProducts;
    }

    public List<Product> getProductsUnderSpecificValue() {
        return productsUnderSpecificValue;
    }

    public void setProductsUnderSpecificValue(List<Product> productsUnderSpecificValue) {
        this.productsUnderSpecificValue = productsUnderSpecificValue;
    }

    public List<String> getHotTrendingCategories() {
        return hotTrendingCategories;
    }

    public void setHotTrendingCategories(List<String> hotTrendingCategories) {
        this.hotTrendingCategories = hotTrendingCategories;
    }
}
