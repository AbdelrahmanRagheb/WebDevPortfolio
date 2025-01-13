package org.abdelrahman.blogify.repository;

import org.abdelrahman.blogify.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCategoryRepository  extends JpaRepository<PostCategory, Long> {
}
