package com.swp391.eyewear_management_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)  //để khi trả về response, field nào NULL thì sẽ ko cần trả về (ko hiển thị)
public class ApiResponse<T> {
    @Builder.Default
    int code = 1000;
    String message;
    T result;
}
