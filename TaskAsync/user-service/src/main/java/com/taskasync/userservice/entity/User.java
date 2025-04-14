package com.taskasync.userservice.entity;

import jakarta.persistence.*; // Or javax.persistence.* if using older Spring Boot/Java EE
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"keycloak_subject_id"}),
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"username"})
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {


    @Column(name = "keycloak_subject_id", nullable = false, unique = true, length = 255)
    @NotNull(message = "Keycloak Subject ID cannot be null")
    @NotBlank
    private String keycloakSubjectId;

    @Column(name = "username", unique = true, length = 255)
    private String username;

    @Column(name = "email", unique = true)
    private String email;



    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name", length = 510)
    private String fullName;



}