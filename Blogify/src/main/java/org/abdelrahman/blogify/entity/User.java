package org.abdelrahman.blogify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    @Pattern(regexp = "^(?![0-9])[a-zA-Z0-9]+$", message = "Username must not start with a number and must not contain spaces")
    private String username;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "bio")
    private String bio;

    @CreationTimestamp
    @Column(name = "created_at")
    private String createdAt;

    @OneToMany(mappedBy = "author",fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<PostLike> likes;

    public void addPost(Post post) {
        if (this.posts == null) {
            this.posts = new ArrayList<>();
        }
        post.setAuthor(this);
        posts.add(post);
    }

    public void addPostLike(PostLike postLike) {
        if (this.likes == null) {
            this.likes = new ArrayList<>();
        }
        postLike.setUser(this);
        likes.add(postLike);
    }


}
