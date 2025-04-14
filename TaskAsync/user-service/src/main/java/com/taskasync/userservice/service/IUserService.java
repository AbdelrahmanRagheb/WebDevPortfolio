package com.taskasync.userservice.service;

import com.taskasync.userservice.dto.UserDto;

public interface IUserService {
    Long createNewUser(UserDto userDto);
    boolean checkUserExists(String keycloakId);
    Long getUserIdByKeycloakId(String keycloakId);
    UserDto getUserByKeycloakId(String keycloakId);
}