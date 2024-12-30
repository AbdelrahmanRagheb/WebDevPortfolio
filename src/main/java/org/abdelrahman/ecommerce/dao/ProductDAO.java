package org.abdelrahman.ecommerce.dao;

import org.abdelrahman.ecommerce.entity.Product;
import org.abdelrahman.ecommerce.entity.Review;
import org.abdelrahman.ecommerce.model.HomePageModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ProductDAO {
     void addProduct(Product product);
     void updateProduct(Product product);
     void deleteProduct(Product product);
     Product getProduct(Long id);
     List<Product> getNProduct(int n);


     List<Product> getProductsByCategory(String category);
     List<Product> getProductsByBrand(String brand);
     List<Product> getProductsByCategoryAndBrand(String category, String brand);

     List<Product> searchProducts(String keyword);
     List<Review> getReviewsOnProduct(Long productId);
     List<Product> getProductsUnderPrice(int size,double price);
     List<Product> getProductsUnderPrice(double price);
     List<Product> getTopRatedProducts(int size);
     List<Product> getTopRatedProducts();
     List<String> getTopCategories();

   //  boolean isProductLikedByUser(Long userId, Long productId);


}
