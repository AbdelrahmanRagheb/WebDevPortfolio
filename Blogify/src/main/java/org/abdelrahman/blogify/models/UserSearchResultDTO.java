package org.abdelrahman.blogify.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSearchResultDTO {
    private String username;
    private String bio;
    private String role;
    private String createdAt;
}