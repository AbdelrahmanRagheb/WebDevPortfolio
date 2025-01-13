package org.abdelrahman.blogify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String content;

    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    private Set<PostCategory> postCategories;

    public void addPostCategory(PostCategory pc) {
        if (postCategories == null) {
            postCategories = new HashSet<>();
        }
        pc.setCategory(this);
        postCategories.add(pc);
    }

}
