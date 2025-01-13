package org.abdelrahman.ecommerce.dao;

import org.abdelrahman.ecommerce.entity.User;

import java.util.List;

public interface UserDAO {
    void addNewUser(User user);
    void updateUserInfo(User user);
    void deleteUser(User user);
    User getUserById(int id);
    User getUserByUserName(String username);
    List<User> getUsersList();
    List<Long> getUserLikes(Long userId);






}
