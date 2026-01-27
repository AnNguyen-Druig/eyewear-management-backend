package com.swp391.eyewear_management_backend.mapper;


import com.swp391.eyewear_management_backend.dto.request.RoleRequest;
import com.swp391.eyewear_management_backend.dto.response.RoleResponse;
import com.swp391.eyewear_management_backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
