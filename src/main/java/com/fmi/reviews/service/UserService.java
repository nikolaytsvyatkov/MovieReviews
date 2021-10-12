package com.fmi.reviews.service;

import com.fmi.reviews.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();
    User deleteUser(Long id);
    User getUser(Long id);
    User addUser(User user);
    User updateUser(Long id, User user);
    User getUserByUsername(String username);
    long getUsersCount();
}
