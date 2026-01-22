package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
