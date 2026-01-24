package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.entity.User;
import com.swp391.eyewear_management_backend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface UserService {

    public User findByName(String userName);
}
