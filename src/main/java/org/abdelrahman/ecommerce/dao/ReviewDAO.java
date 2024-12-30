package org.abdelrahman.ecommerce.dao;

import org.abdelrahman.ecommerce.entity.Review;

import java.util.List;

public interface ReviewDAO {

    Review findReview(Long reviewId);
    void updateReview(Review review);
    void deleteReview(Review review);

}
