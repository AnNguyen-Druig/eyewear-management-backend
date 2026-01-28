package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    public List<RoleResponse> getRoles();
}
