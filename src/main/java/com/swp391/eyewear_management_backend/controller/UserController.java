package com.swp391.eyewear_management_backend.controller;

import com.swp391.eyewear_management_backend.entity.User;
import com.swp391.eyewear_management_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        System.out.println("Fetching User with name " + name);
        User user = userService.findByName(name);
        if (user == null) {
            System.out.println("User with name " + name + " not found");
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
}
