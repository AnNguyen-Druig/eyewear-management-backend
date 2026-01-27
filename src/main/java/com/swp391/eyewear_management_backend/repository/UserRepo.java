package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "show-all-users")
public interface UserRepo extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    public User findByName(String name);

    Optional<User> findByUsername(String username);
}
