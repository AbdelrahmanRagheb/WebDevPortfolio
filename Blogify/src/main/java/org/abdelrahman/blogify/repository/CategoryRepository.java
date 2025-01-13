package org.abdelrahman.blogify.repository;

import org.abdelrahman.blogify.entity.Category;

import org.abdelrahman.blogify.entity.Tag;
import org.abdelrahman.blogify.models.CategoryDTO;
import org.abdelrahman.blogify.models.TagSearchResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Category> searchCategories(@Param("query") String query, Pageable pageable);

    @Query("SELECT pc.category.id, pc.post.id FROM PostCategory pc WHERE pc.category.id IN :categoryIds")
    Page<Object[]> findPostIdsByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);
}
