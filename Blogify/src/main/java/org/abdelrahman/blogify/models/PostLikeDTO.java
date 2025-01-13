package org.abdelrahman.blogify.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.abdelrahman.blogify.entity.Comment;
import org.abdelrahman.blogify.entity.PostLike;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostLikeDTO {
    private Long id;
    private String createdAt;
    private Long liker;
//    private Long postId;


    public static PostLikeDTO convertToDTO(PostLike postLike) {
        // Create a new CommentDTO instance
        PostLikeDTO postLikeDTO = new PostLikeDTO();
        postLikeDTO.setId(postLike.getId());
        postLikeDTO.setCreatedAt(postLike.getCreatedAt());
        postLikeDTO.setLiker(postLike.getUser().getId());
//        postLikeDTO.setPostId(postLike.getPost().getId());
        return postLikeDTO;
    }

}
