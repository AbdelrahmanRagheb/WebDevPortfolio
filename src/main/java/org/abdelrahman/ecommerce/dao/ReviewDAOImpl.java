package org.abdelrahman.ecommerce.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.abdelrahman.ecommerce.entity.Product;
import org.abdelrahman.ecommerce.entity.Review;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ReviewDAOImpl implements ReviewDAO {
    private EntityManager entityManager;

    public ReviewDAOImpl(EntityManager entityManager) {this.entityManager = entityManager;}

    @Transactional
    @Override
    public Review findReview(Long reviewId) {
        return entityManager.find(Review.class, reviewId);
    }

    @Transactional
    @Override
    public void updateReview(Review review) {
        entityManager.merge(review);
    }

    @Transactional
    @Override
    public void deleteReview(Review review) {
        entityManager.remove(review);
    }


}
