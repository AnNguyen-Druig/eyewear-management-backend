package com.swp391.eyewear_management_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRespone {
    String id;
    String username;
    String email;
    String phone;
    String name;
    LocalDate dob;
    RoleResponse role;
}


