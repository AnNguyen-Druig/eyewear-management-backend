package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    String username;

    String password;

    String email;

    String phone;

    String name;

    //@DobConstraint(min = 16, message = "INVALID_DOB")    //Custom Annotation
    LocalDate dob;
}

//us, ps, email, phone,