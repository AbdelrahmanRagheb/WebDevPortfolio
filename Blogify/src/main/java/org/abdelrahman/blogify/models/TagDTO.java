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
public class TagDTO {
    private Long tagId;
    private String name;
    private List<Long> postsIdsWithTag= new ArrayList<>();

    public TagDTO(Long tagId,String name){
        this.tagId=tagId;
        this.name=name;
    }

}
