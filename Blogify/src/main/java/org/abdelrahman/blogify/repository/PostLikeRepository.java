package org.abdelrahman.blogify.repository;

import org.abdelrahman.blogify.entity.PostLike;
import org.abdelrahman.blogify.models.PostLikeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository  extends JpaRepository<PostLike, Long> {



    @Query("select new org.abdelrahman.blogify.models.PostLikeDTO(" +
            "pl.id,pl.createdAt,pl.user.id)" +
            "from PostLike pl where pl.post.id=?1")
    List<PostLikeDTO> getLikesByPostId(Long postId);
}
