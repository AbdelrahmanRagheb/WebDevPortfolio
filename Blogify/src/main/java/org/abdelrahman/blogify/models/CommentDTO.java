package org.abdelrahman.blogify.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.abdelrahman.blogify.entity.Comment;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDTO {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;


    public static CommentDTO convertToDTO(Comment comment) {
        // Create a new CommentDTO instance
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setAuthorId(comment.getUser().getId());
        commentDTO.setPostId(comment.getPost().getId());
        return commentDTO;
    }

}
