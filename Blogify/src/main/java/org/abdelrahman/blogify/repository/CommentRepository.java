package org.abdelrahman.blogify.repository;

import org.abdelrahman.blogify.entity.Comment;
import org.abdelrahman.blogify.models.CommentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select new org.abdelrahman.blogify.models.CommentDTO(" +
            "c.id, c.content,c.user.id,c.post.id)" +
            " from Comment c where c.post.id=?1")
    List<CommentDTO> findAllCommentsByPostId(Long postId);
}
