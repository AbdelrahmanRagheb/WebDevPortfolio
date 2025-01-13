package org.abdelrahman.ecommerce.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.abdelrahman.ecommerce.entity.Review;
import org.abdelrahman.ecommerce.model.HomePageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.abdelrahman.ecommerce.entity.Product;
import org.springframework.stereotype.Repository;


import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductDAOImpl implements ProductDAO {

    private EntityManager entityManager;

    @Autowired
    public ProductDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public void addProduct(Product product) {
        entityManager.persist(product);
    }

    @Transactional
    @Override
    public void updateProduct(Product product) {
        entityManager.merge(product);
    }

    @Override
    public void deleteProduct(Product product) {
        entityManager.remove(product);
    }

    @Transactional
    @Override
    public Product getProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID to load is required for loading");
        }
        return entityManager.find(Product.class, id);
    }

    @Override
    public List<Product> getNProduct(int n) {
        TypedQuery<Product> query = entityManager.createQuery("select p from Product p ", Product.class);
        query.setMaxResults(n);
        return query.getResultList();
    }


    @Transactional
    @Override
    public List<Product> getProductsByCategory(String category) {
        TypedQuery<Product> query = entityManager.createQuery("Select p from Product p where p.category=:category", Product.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Transactional
    @Override
    public List<Product> getProductsByBrand(String brand) {
        TypedQuery<Product> query = entityManager.createQuery("select p from Product p where p.brand=:brand", Product.class);
        query.setParameter("brand", brand);
        return query.getResultList();
    }

    @Transactional
    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        TypedQuery<Product> query = entityManager.createQuery("select p from Product p where p.category=:category and p.brand=:brand", Product.class);
        query.setParameter("category", category);
        query.setParameter("brand", brand);
        return query.getResultList();
    }

    @Transactional
    @Override
    public List<Product> searchProducts(String keyword) {
        List<Product> searchRes = handleMisspellings(keyword, "natural language");
        if (searchRes.isEmpty()) {
            return handleMisspellings(keyword + "*", "boolean");
        }
        return searchRes;
    }

    @Override
    public List<Review> getReviewsOnProduct(Long productId) {
        TypedQuery<Review> query = entityManager.createQuery("SELECT r FROM Review r WHERE r.product.id = :productId", Review.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }


    @Override
    public List<Product> getProductsUnderPrice(double price) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.price < :price order by p.price asc ", Product.class);
        query.setParameter("price", price);

        return query.getResultList();
    }

    @Override
    public List<Product> getProductsUnderPrice(int size,double price) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.price < :price order by p.price asc ", Product.class);
        query.setParameter("price", price);
        query.setMaxResults(size);
        return query.getResultList();
    }


    @Transactional
    @Override
    public List<Product> getTopRatedProducts(int size) {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.ratingCount > 0 ORDER BY (p.rating / p.ratingCount) DESC", Product.class);
        query.setMaxResults(size);
        return query.getResultList();
    }
    @Transactional
    @Override
    public List<Product> getTopRatedProducts() {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.ratingCount > 0 ORDER BY (p.rating / p.ratingCount) DESC", Product.class);

        return query.getResultList();
    }


    @Override
    public List<String> getTopCategories() {
        // JPA query to get top categories with product count
        TypedQuery<String> query = entityManager.createQuery(
                "SELECT p.category FROM Product p " +
                        "GROUP BY p.category " +
                        "ORDER BY COUNT(p) DESC", String.class);

        query.setMaxResults(5);  // Limiting to top 5 categories

        return query.getResultList();
    }


    private List<Product> handleMisspellings(String keyword, String mode) {
        String queryString;
        if ("boolean".equalsIgnoreCase(mode)) {
            queryString = "SELECT * FROM products WHERE MATCH(title, `desc`, brand, category) AGAINST (?1 IN BOOLEAN MODE)";
        } else {
            queryString = "SELECT * FROM products WHERE MATCH(title, `desc`, brand, category) AGAINST (?1 IN NATURAL LANGUAGE MODE)";
        }

        Query nativeQuery = entityManager.createNativeQuery(queryString, Product.class);
        nativeQuery.setParameter(1, keyword);
        return nativeQuery.getResultList();
    }

}
