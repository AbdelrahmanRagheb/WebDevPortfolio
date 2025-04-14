package com.taskasync.gateway_server.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSyncRequest {
    private String keycloakId;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
}