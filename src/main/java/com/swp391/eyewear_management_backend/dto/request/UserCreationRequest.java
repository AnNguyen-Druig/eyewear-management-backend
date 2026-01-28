package com.swp391.eyewear_management_backend.dto.request;

import com.swp391.eyewear_management_backend.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @NotBlank(message = "USERNAME_REQUIRED")
    @Size(min = 8, message = "USERNAME_INVALID")
    String username;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "PASSWORD_INVALID"
    )
    String password;

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(regexp = "^\\d{10,11}$", message = "PHONE_INVALID")
    String phone;

    @NotBlank(message = "NAME_REQUIRED")
    String name;

    //@DobConstraint(min = 1, message = "INVALID_DOB")  --> Custom Annotation
    @Past(message = "DOB_INVALID") // yyyy-mm-dd và phải trước hôm nay (LocalDate parse + @Past)
    LocalDate dob;

    // optional: nếu có nhập thì phải hợp lệ (12 số). Nếu null thì ok.
    @Pattern(regexp = "^\\d{12}$", message = "IDNUMBER_INVALID")
    String address;

    @Pattern(regexp = "^(?!\\s*$).+", message = "ADDRESS_INVALID")
    String idNumber;
}

//us, ps, email, phone,