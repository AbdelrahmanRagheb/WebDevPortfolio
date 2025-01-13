package org.abdelrahman.blogify.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String role;
    private String createdAt;
    private List<Long> postsId;
    private int numberOfPosts;

    public UserDTO(Long id, String username, String email, String bio, String role, String createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.role = role;
        this.createdAt = createdAt;
    }
}
