package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.response.RoleResponse;
import com.swp391.eyewear_management_backend.mapper.RoleMapper;
import com.swp391.eyewear_management_backend.repository.RoleRepo;
import com.swp391.eyewear_management_backend.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor        //Tiêm Bean vào Class bằng CTOR -> Khỏi @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)   //để kiểu private final tự động cho các field cần tiêm Bean vào class
@Slf4j
public class RoleServiceImpl implements RoleService {

    RoleRepo roleRepo;
    RoleMapper roleMapper;

    @Override
    public List<RoleResponse> getRoles() {
        return roleRepo.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }
}
