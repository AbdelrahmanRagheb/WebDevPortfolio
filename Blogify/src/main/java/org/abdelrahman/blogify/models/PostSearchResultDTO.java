package org.abdelrahman.blogify.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostSearchResultDTO {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private Long authorId;
}
