package org.abdelrahman.blogify.repository;

import org.abdelrahman.blogify.entity.Tag;
import org.abdelrahman.blogify.entity.User;
import org.abdelrahman.blogify.models.PostTagDTO;
import org.abdelrahman.blogify.models.TagDTO;
import org.abdelrahman.blogify.models.TagSearchResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query(value = "SELECT * FROM tag t WHERE t.name LIKE %:query%", nativeQuery = true)
    Page<Tag> searchTags(@Param("query") String query, Pageable pageable);

    @Query("select new org.abdelrahman.blogify.models.TagDTO(" +
            "t.id,t.name)" +
            " from Tag t")
    List<TagDTO> findAllTags();

    @Query(value = "SELECT t.name AS name, p.id AS postId FROM post_tag pt " +
                    "JOIN tag t ON pt.tag_id = t.id " +
                    "JOIN blog_post p ON pt.post_id = p.id " +
                    "WHERE t.id IN (:tagIds)"
            , countQuery = "SELECT count(*) FROM post_tag pt WHERE pt.tag_id IN (:tagIds)"
            , nativeQuery = true)
    Page<TagSearchResultDTO> findAllPostsByTagIds(@Param("tagIds") List<Long> tagIds, Pageable pageable);

    @Query("select new org.abdelrahman.blogify.models.PostTagDTO(" +
            "pt.id,pt.post.id,pt.tag.id)" +
            "from PostTag pt ")
    List<PostTagDTO> findAllTagsInfo();

    @Query("select pt.post.id from PostTag pt where pt.tag.id=?1")
    List<Long> findAllPostsByTagId(Long tagId);


    @Query("select new org.abdelrahman.blogify.models.TagDTO(" +
            "t.id , t.name )" +
            " from Tag t where t.name=?1")
    TagDTO getTagByName(String name);

}
