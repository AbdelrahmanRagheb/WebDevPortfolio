package org.abdelrahman.ecommerce.model;



import java.util.*;

public class HomePageView {
    List<ProductView> topRatedProducts;
    List<ProductView> productsUnderSpecificValue;
    List<String> hotTrendingCategories;

    public HomePageView() {
    }

    public HomePageView(List<ProductView> topRatedProducts, List<ProductView> productsUnderSpecificValue,List<String> hotTrendingCategories) {
        this.topRatedProducts = topRatedProducts;
        this.productsUnderSpecificValue = productsUnderSpecificValue;
        this.hotTrendingCategories = hotTrendingCategories;
    }

    public List<ProductView> getTopRatedProducts() {
        return topRatedProducts;
    }

    public void setTopRatedProducts(List<ProductView> topRatedProducts) {
        this.topRatedProducts = topRatedProducts;
    }

    public List<ProductView> getProductsUnderSpecificValue() {
        return productsUnderSpecificValue;
    }

    public void setProductsUnderSpecificValue(List<ProductView> productsUnderSpecificValue) {
        this.productsUnderSpecificValue = productsUnderSpecificValue;
    }

    public List<String> getHotTrendingCategories() {
        return hotTrendingCategories;
    }

    public void setHotTrendingCategories(List<String> hotTrendingCategories) {
        this.hotTrendingCategories = hotTrendingCategories;
    }
}
