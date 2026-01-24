package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.entity.User;
import com.swp391.eyewear_management_backend.repository.UserRepo;
import com.swp391.eyewear_management_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public User findByName(String userName) {
        return userRepo.findByName(userName);
    }
}
