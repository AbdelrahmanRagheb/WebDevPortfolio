package org.abdelrahman.blogify.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor

@Getter
@Setter
public class PostSummaryDTO {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private Long authorId;
    private List<Long> commentsIds;
    private List<Long> likesIds;
    private List<Long> tagsIds;
    private List<Long> categoriesIds;
    public PostSummaryDTO(Long id, String title, String content, String createdAt, Long authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.authorId = authorId;
    }


}
