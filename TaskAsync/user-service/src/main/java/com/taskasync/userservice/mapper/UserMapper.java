package com.taskasync.userservice.mapper;

import com.taskasync.userservice.dto.UserDto;
import com.taskasync.userservice.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user, UserDto userDto) {

        userDto.setKeycloakSubjectId(user.getKeycloakSubjectId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setFullName(user.getFullName());
        userDto.setUsername(user.getUsername());

        return userDto;
    }

    public static User mapToUser(UserDto userDto, User user) {


        user.setKeycloakSubjectId(userDto.getKeycloakSubjectId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFullName(userDto.getFullName());
        user.setUsername(userDto.getUsername());

        return user;
    }
}
