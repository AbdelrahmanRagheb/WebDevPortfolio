package org.abdelrahman.blogify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "blog_post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;


    @CreationTimestamp
    @Column(name = "created_at")
    private String createdAt;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "post",fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post",fetch = FetchType.LAZY)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "post",fetch = FetchType.LAZY)
    private Set<PostCategory> categories;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "post",fetch = FetchType.LAZY)
    private Set<PostTag> postTags;

    public void addPostTag(PostTag postTag) {
        if (postTags == null) {
            postTags = new HashSet<PostTag>();
        }
        postTag.setPost(this);
        postTags.add(postTag);
    }

    public void addPostToNewCategory(PostCategory category) {
        if (this.categories == null) {
            this.categories = new HashSet<>();
        }
        category.setPost(this);
        categories.add(category);
    }
}
