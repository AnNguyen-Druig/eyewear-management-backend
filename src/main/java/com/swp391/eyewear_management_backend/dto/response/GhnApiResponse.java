package com.swp391.eyewear_management_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GhnApiResponse<T> {

    private Integer code;
    private String message;

    @JsonProperty("data")
    private T data;
}
