package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;      // optional: nếu null thì không đổi

    String email;         // optional (thường cần verify nếu đổi)
    String phone;         // optional
    String name;          // optional
    LocalDate dob;        // optional
    String address;       // optional (nếu bạn muốn cho đổi)
}
