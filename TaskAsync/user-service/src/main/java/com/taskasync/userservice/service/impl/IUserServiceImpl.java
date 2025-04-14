package com.taskasync.userservice.service.impl;

import com.taskasync.userservice.dto.UserDto;
import com.taskasync.userservice.entity.User;
import com.taskasync.userservice.mapper.UserMapper;
import com.taskasync.userservice.repository.UserRepository;
import com.taskasync.userservice.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class IUserServiceImpl implements IUserService {

    private UserRepository userRepository;

    @Override
    public Long createNewUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto, new User());
        return userRepository.save(user).getId();
    }

    @Override
    public boolean checkUserExists(String keycloakId) {
        return userRepository.existsByKeycloakSubjectId(keycloakId);
    }

    @Override
    public Long getUserIdByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakSubjectId(keycloakId)
                .map(User::getId)
                .orElse(null); // Return null if no user is found
    }

    @Override
    public UserDto getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakSubjectId(keycloakId)
                .map(user -> UserMapper.mapToUserDto(user, new UserDto()))
                .orElse(null); // Return null if no user is found
    }
}