package org.abdelrahman.blogify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "tag", fetch = FetchType.LAZY)
    private Set<PostTag> postTags;

    public void addPostTag(PostTag postTag) {
        if (postTags == null) {
            postTags = new HashSet<PostTag>();
        }
        postTag.setTag(this);
        postTags.add(postTag);
    }

}
