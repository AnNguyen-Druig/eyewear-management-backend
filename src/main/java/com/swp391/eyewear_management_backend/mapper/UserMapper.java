package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.request.UserCreationRequest;
import com.swp391.eyewear_management_backend.dto.request.UserUpdateRequest;
import com.swp391.eyewear_management_backend.dto.response.UserRespone;
import com.swp391.eyewear_management_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = RoleMapper.class)     //tiêm RoleMapper vào UserMapper để trả về role.typeName khi UserResponse cần RoleResponse roleResponse
public interface UserMapper {

    @Mapping(source = "dob", target = "dateOfBirth")
    User toUser(UserCreationRequest request);

    @Mapping(source = "userID", target = "id")
    @Mapping(source = "dateOfBirth", target = "dob")
    UserRespone toUserRespone(User user);

    //@Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
