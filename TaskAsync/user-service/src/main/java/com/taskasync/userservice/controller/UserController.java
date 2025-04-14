package com.taskasync.userservice.controller;

import com.taskasync.userservice.dto.ResponseDto;
import com.taskasync.userservice.dto.UserDto;
import com.taskasync.userservice.service.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private IUserService iUserService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/signup")
    public ResponseEntity<Long> createNewUser(@Valid @RequestBody UserDto userDto) {
        Long userId = iUserService.createNewUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @GetMapping("/keycloak/{keycloakId}/id")
    public ResponseEntity<Long> getUserIdByKeycloakId(@PathVariable
                                                      @NotBlank
                                                      @NotNull(message = "Keycloak Subject ID cannot be null")
                                                      String keycloakId) {
        log.info("Fetching user ID for keycloakId: {}", keycloakId);
        Long userId = iUserService.getUserIdByKeycloakId(keycloakId);
        if (userId == null) {
            log.info("No user found for keycloakId: {}", keycloakId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/exists/{keycloakId}")
    public ResponseEntity<ResponseDto> checkUserExists(@PathVariable
                                                       @NotBlank
                                                       @NotNull(message = "Keycloak Subject ID cannot be null")
                                                       String keycloakId) {
        log.info("Checking user existence for keycloakId: {}", keycloakId);
        boolean userExists = iUserService.checkUserExists(keycloakId);
        log.info("User exists: {}", userExists);
        return ResponseEntity.ok(new ResponseDto(
                userExists ? "200" : "404",
                userExists ? "user id exists" : "user with this id does not exist"
        ));
    }

    @GetMapping("/fetch/{keycloakId}")
    public ResponseEntity<UserDto> fetchUserDetails(@PathVariable
                                                    @NotBlank
                                                    @NotNull(message = "Keycloak Subject ID cannot be null")
                                                    String keycloakId) {
        log.info("Fetching user details by keycloakId: {}", keycloakId);
        UserDto user = iUserService.getUserByKeycloakId(keycloakId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

}