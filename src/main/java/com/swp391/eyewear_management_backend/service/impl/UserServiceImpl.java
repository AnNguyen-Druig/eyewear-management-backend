package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.request.UserCreationRequest;
import com.swp391.eyewear_management_backend.dto.request.UserUpdateRequest;
import com.swp391.eyewear_management_backend.dto.response.UserRespone;
import com.swp391.eyewear_management_backend.entity.Role;
import com.swp391.eyewear_management_backend.entity.User;
import com.swp391.eyewear_management_backend.exception.AppException;
import com.swp391.eyewear_management_backend.exception.ErrorCode;
import com.swp391.eyewear_management_backend.mapper.UserMapper;
import com.swp391.eyewear_management_backend.repository.RoleRepo;
import com.swp391.eyewear_management_backend.repository.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor        //Tiêm Bean vào Class bằng CTOR -> Khỏi @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)   //để kiểu private final tự động cho các field cần tiêm Bean vào class
@Slf4j
public class UserServiceImpl {

    UserRepo userRepo;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepo roleRepo;

    public UserRespone createRequest(UserCreationRequest request) {

        if(userRepo.existsByUsername(request.getUsername())) throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //user.setRoles(roles);

        return userMapper.toUserRespone(userRepo.save(user));
    }

    public UserRespone getMyInfo() {
        var context = SecurityContextHolder.getContext();       //get User hiện tại
        String name = context.getAuthentication().getName();    //lấy ra name của user đang request

        User user = userRepo.findByUsername(name).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));          //Kiểm tra xem có ko, nếu có thì hiển thị, nếu không thì throw Exception

        return userMapper.toUserRespone(user);
    }

    //Phân quyền dựa trên Method
    @PreAuthorize("hasRole('ADMIN')")   //@PreAuthorize("hasRole('X')") sẽ chặn các user mà có role ko trùng với role X     --> Thỏa Method mới đc vào method
    //@PreAuthorize("hasAuthority('UPDATE_POST')")
    public List<UserRespone> getUsers() {
        log.info("In method getUsers");
        return userRepo.findAll().stream().map(userMapper::toUserRespone).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")  //@PostAuthorize("hasRole('Y')") sẽ cho phép user chạy hàm này, nhưng sau khi chạy xong sẽ kiểm tra, và nếu user có role ko trùng với Y thì sẽ bị chặn và ko hiển thị kết quả --> Sau khi thực hiện xong, nếu thỏa thì mới đc sử dụng (hiển thị kết quả), nếu ko thì chặn
    //@PostAuthorize sẽ được dùng khi để 1 user chỉ được xem thông tin của chính mình, ko xem được bất kì thông tin nào của người khác
    public UserRespone getUserById(Long id) {
        log.info("In method getUserById");
        return userMapper.toUserRespone(userRepo.findById(id).orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTED.getMessage())));
    }

    public UserRespone updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTED.getMessage()));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        return userMapper.toUserRespone(userRepo.save(user));
    }

    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }
}
