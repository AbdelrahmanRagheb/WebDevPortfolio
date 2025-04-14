package com.taskasync.userservice.dto;

import jakarta.validation.constraints.Email; // Or javax.validation.constraints.*
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {


    private Long id;

    @NotBlank(message = "Keycloak Subject ID cannot be blank")
    @Size(max = 255, message = "Keycloak Subject ID cannot exceed 255 characters")
    private String keycloakSubjectId;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 255, message = "Username cannot exceed 255 characters")
    private String username;

    @Size(max = 255, message = "First name cannot exceed 255 characters")
    private String firstName;

    @Size(max = 255, message = "Last name cannot exceed 255 characters")
    private String lastName;

    @Size(max = 510, message = "Full name cannot exceed 510 characters")
    private String fullName;
}