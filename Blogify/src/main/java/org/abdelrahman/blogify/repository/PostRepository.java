package org.abdelrahman.blogify.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.abdelrahman.blogify.entity.Post;
import org.abdelrahman.blogify.models.PostSummaryDTO;
import org.abdelrahman.blogify.models.UserIdPostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * FROM blog_post p WHERE MATCH(p.title, p.content) AGAINST (?1 IN BOOLEAN MODE)", nativeQuery = true)
    Page<Post> searchPosts(String query, Pageable pageable);

    @Query("SELECT new org.abdelrahman.blogify.models.PostSummaryDTO(" +
            "p.id, p.title, p.content, p.createdAt, p.author.id) " + "FROM Post p")
    Page<PostSummaryDTO> findAllPosts(Pageable pageable);

    @Query("select " + "new org.abdelrahman.blogify.models.PostSummaryDTO(p.id, p.title , p.content ," + "p.createdAt, p.author.id ) " + "from Post p where p.id = ?1")
    PostSummaryDTO findPostById(Long id);

    @Query("select p from Post p where p.id = ?1")
    Post findAllPostInfoById(Long id);

    @Query("select p.id from Post p where p.author.id =?1")
    List<Long> getPostsIdsByUserId(Long authorId);

    @Query("select new org.abdelrahman.blogify.models.UserIdPostId(p.id , p.author.id) from Post p")
    List<UserIdPostId> findUserIdAndPostId();

    @Query("select c.id from  Comment c where c.post.id=?1")
    List<Long> findCommentsIdsByPostId(Long postId);

    @Query("select pl.user.id from  PostLike pl where pl.post.id=?1")
    List<Long> findLikesIdsByPostId(Long postId);

    @Query("select pt.tag.id from  PostTag pt where pt.post.id=?1")
    List<Long> findTagsIdsByPostId(Long postId);

    @Query("select pc.category.id from  PostCategory pc where pc.post.id=?1")
    List<Long> findCategoriesIdsByPostId(Long postId);


    @Query("SELECT c.post.id AS postId, c.id FROM Comment c WHERE c.post.id IN ?1")
    List<Object[]> findCommentIdsByPostIds(List<Long> postIds);

    @Query("SELECT pl.post.id AS postId, pl.user.id FROM PostLike pl WHERE pl.post.id IN ?1")
    List<Object[]> findLikeIdsByPostIds(List<Long> postIds);

    @Query("SELECT pt.post.id AS postId, pt.tag.id FROM PostTag pt WHERE pt.post.id IN ?1")
    List<Object[]> findTagIdsByPostIds(List<Long> postIds);

    @Query("SELECT pc.post.id AS postId, pc.category.id FROM PostCategory pc WHERE pc.post.id IN ?1")
    List<Object[]> findCategoryIdsByPostIds(List<Long> postIds);


}
