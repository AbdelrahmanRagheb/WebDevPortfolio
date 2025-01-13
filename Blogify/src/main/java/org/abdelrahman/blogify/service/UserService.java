package org.abdelrahman.blogify.service;

import org.abdelrahman.blogify.models.UserDTO;
import org.abdelrahman.blogify.entity.User;
import org.abdelrahman.blogify.exception.DuplicateUsernameException;
import org.abdelrahman.blogify.exception.UserNotFoundException;
import org.abdelrahman.blogify.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostService postService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, PostService postService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }

    public User addNewUser(User user) {
        try {

            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateUsernameException("Username '" + user.getUsername() + "' already exists");
        }
    }


    public UserDTO getUserInfo(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " does not exist");
        }
        UserDTO userDTO = userRepository.findUserById(id);
        List<Long> postsIds = postService.getPostsIdsByUserId(id);
        userDTO.setPostsId(postsIds);
        userDTO.setNumberOfPosts(postsIds.size());
        return userDTO;

    }

    public User getAllUserInfoById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " does not exist");
        }
        return userRepository.findAllUserInfoById(id);

    }


    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("User with ID " + user.getId() + " does not exist");
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with ID " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }


    public List<UserDTO> getAllUsersInfo() {
        List<UserDTO> users = userRepository.findAllUsers();
        HashMap<Long, List<Long>> userPostsMap = postService.groupPostIdsByUser();
        users.forEach(user -> {
            Long userId = user.getId();
            if (userPostsMap.containsKey(userId)) {
                user.setPostsId(userPostsMap.get(userId));
                user.setNumberOfPosts(userPostsMap.get(userId).size());
            } else {
                user.setPostsId(new ArrayList<>());
                user.setNumberOfPosts(0);
            }
        });
        return users;
    }
}
