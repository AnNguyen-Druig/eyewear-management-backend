package com.swp391.eyewear_management_backend.controller;

import com.swp391.eyewear_management_backend.dto.request.UserCreationRequest;
import com.swp391.eyewear_management_backend.dto.request.UserUpdateRequest;
import com.swp391.eyewear_management_backend.dto.response.ApiResponse;
import com.swp391.eyewear_management_backend.dto.response.UserRespone;
import com.swp391.eyewear_management_backend.service.UserService;
import com.swp391.eyewear_management_backend.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    //UserService userService;
    UserServiceImpl userServiceImpl;

    @PostMapping
    ApiResponse<UserRespone> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<UserRespone> apiRespone = new ApiResponse<>();

        apiRespone.setResult(userServiceImpl.createRequest(request));

        return apiRespone;
    }

    @GetMapping
    ApiResponse<List<UserRespone>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username : {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("Authority : {}", grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserRespone>>builder()
                .result(userServiceImpl.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserRespone> getUser(@PathVariable Long userId) {
        return ApiResponse.<UserRespone>builder()
                .result(userServiceImpl.getUserById(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserRespone> getMyInfo() {
        return ApiResponse.<UserRespone>builder()
                .result(userServiceImpl.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    UserRespone updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateRequest request) {
        return userServiceImpl.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public String deleteUserById(@PathVariable Long userId) {
        userServiceImpl.deleteUserById(userId);
        return "User has been deleted";
    }
}
