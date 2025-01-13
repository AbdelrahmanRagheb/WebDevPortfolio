package org.abdelrahman.blogify.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TagSearchResultDTO {
    private String name;
    private Long postId;
    private List<Long> postsIdsWithTag;

    public TagSearchResultDTO(String name, Long postId) {
        this.name = name;
        this.postId = postId;
    }
}
