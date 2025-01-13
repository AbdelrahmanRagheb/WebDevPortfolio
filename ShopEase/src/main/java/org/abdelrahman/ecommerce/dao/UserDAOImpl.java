package org.abdelrahman.ecommerce.dao;

import jakarta.persistence.EntityManager;

import jakarta.persistence.TypedQuery;
import org.abdelrahman.ecommerce.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    private EntityManager entityManager;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    UserDAOImpl(EntityManager entityManager, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.entityManager = entityManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @Override
    public void addNewUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setAccountType("ROLE_" + user.getAccountType());
        entityManager.persist(user);
    }

    @Transactional
    @Override
    public void updateUserInfo(User user) {
        System.out.println("++++++++++++++++++++++++++++::updateUserInfo() called::+++++++++++++++++++++++++++");
        entityManager.merge(user);

    }

    @Override
    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    @Transactional
    @Override
    public User getUserById(int id) {

        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.likes WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Transactional
    @Override
    public User getUserByUserName(String username) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.likes WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    @Transactional
    @Override
    public List<User> getUsersList() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    @Override
    public List<Long> getUserLikes(Long userId) {
        TypedQuery<Long> query = entityManager.createQuery("select u.likes from User u where u.id=:id", Long.class);
        query.setParameter("id", userId);
        return query.getResultList();
    }


}
