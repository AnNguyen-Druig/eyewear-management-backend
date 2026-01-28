package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.request.UserCreationRequest;
import com.swp391.eyewear_management_backend.dto.request.UserUpdateRequest;
import com.swp391.eyewear_management_backend.dto.response.UserRespone;
import com.swp391.eyewear_management_backend.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {
    @Mapping(source = "dob", target = "dateOfBirth")
    User toUser(UserCreationRequest request);

    @Mapping(source = "dateOfBirth", target = "dob")
    UserRespone toUserRespone(User user);

    //@Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
