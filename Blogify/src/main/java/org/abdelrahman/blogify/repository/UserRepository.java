package org.abdelrahman.blogify.repository;

import org.abdelrahman.blogify.entity.Post;
import org.abdelrahman.blogify.models.UserDTO;
import org.abdelrahman.blogify.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM User u WHERE MATCH(u.username, u.bio) AGAINST (?1 IN BOOLEAN MODE)", nativeQuery = true)
    Page<User> searchUsers(String query, Pageable pageable);
    @Query("select new org.abdelrahman.blogify.models.UserDTO(" +
            "u.id , u.username , u.email, u.bio, u.role, u.createdAt) " +
            "from User u where u.id = ?1")
    UserDTO findUserById(Long id);
    @Query("select u from User u where u.id = ?1")
    User findAllUserInfoById(Long id);


    @Query("select new org.abdelrahman.blogify.models.UserDTO(" +
            "u.id , u.username , u.email, u.bio, u.role, u.createdAt) " +
            "from User u ")
    List<UserDTO> findAllUsers();


}
