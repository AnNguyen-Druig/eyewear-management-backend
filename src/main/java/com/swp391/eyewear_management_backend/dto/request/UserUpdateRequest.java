package com.swp391.eyewear_management_backend.dto.request;

import com.swp391.eyewear_management_backend.validator.DobConstraint;
import jakarta.validation.constraints.*;
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

    @Email(message = "EMAIL_INVALID")
    @Pattern(regexp = "^(?!\\s*$).+", message = "EMAIL_INVALID") // chặn "   "
    String email;         // optional (thường cần verify nếu đổi)

    @Pattern(regexp = "^\\d{10,11}$", message = "PHONE_INVALID")
    String phone;         // optional

    @Pattern(regexp = "^(?!\\s*$).+", message = "NAME_INVALID") // chặn "   "
    String name;          // optional

    //@DobConstraint(min = 1, message = "INVALID_DOB")      --> Custom Annotation
    @Past(message = "DOB_INVALID") // yyyy-mm-dd và phải trước hôm nay (LocalDate parse + @Past)
    LocalDate dob;        // optional

    // optional: null ok, nhưng "   " hoặc "" fail
    @Pattern(regexp = "^(?!\\s*$).+", message = "ADDRESS_INVALID")
    String address;       // optional (nếu bạn muốn cho đổi)

    @Pattern(regexp = "^\\d{12}$", message = "IDNUMBER_INVALID")
    String idNumber;

}
