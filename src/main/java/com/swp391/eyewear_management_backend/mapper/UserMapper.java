package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.request.UserCreationRequest;
import com.swp391.eyewear_management_backend.dto.request.UserUpdateRequest;
import com.swp391.eyewear_management_backend.dto.response.UserRespone;
import com.swp391.eyewear_management_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    //@Mapping(source = "firstName", target = "lastName")   --> Map field firstName và lastName thành giống nhau luôn
    //@Mapping(target = "lastName", ignore = true)          --> bỏ qua ko map field lastName --> JSON : "lastName": null
    UserRespone toUserRespone(User user);

    //@Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
