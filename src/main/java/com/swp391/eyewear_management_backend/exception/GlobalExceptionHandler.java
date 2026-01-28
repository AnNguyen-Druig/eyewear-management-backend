package com.swp391.eyewear_management_backend.exception;


import com.swp391.eyewear_management_backend.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception ex) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException ex) {
        ApiResponse apiRespone = new ApiResponse();

        apiRespone.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiRespone.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiRespone);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(
                        ApiResponse.builder()
                                .code(errorCode.getCode())
                                .message(errorCode.getMessage())
                                .build()
                );
    }

//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//        String enumKey = ex.getFieldError().getDefaultMessage();
//
//        ErrorCode errorCode = ErrorCode.INVALID_KEY;
//        Map<String, Object> attributes = null;
//        try {
//            errorCode = ErrorCode.valueOf(enumKey);
//
//            //Kĩ thuật để lấy thông tin User --> Để check Validation chuyên nghiệp, dễ sửa đổi, nâng cấp
//            var constraintViolations = ex.getBindingResult()
//                    .getAllErrors().getFirst().unwrap(ConstraintViolation.class);
//
//            attributes = constraintViolations.getConstraintDescriptor().getAttributes();
//
//            log.info(attributes.toString());
//
//        } catch (IllegalArgumentException e) {
//
//        }
//
//        ApiResponse apiResponse = new ApiResponse();
//        apiResponse.setCode(errorCode.getCode());
//        apiResponse.setMessage(Objects.nonNull(attributes) ?
//                mapAttribute(errorCode.getMessage(), attributes) :
//                errorCode.getMessage());
//
//        return ResponseEntity.badRequest().body(apiResponse);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        var fieldError = ex.getBindingResult().getFieldError();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        if (fieldError != null) {
            String enumKey = fieldError.getDefaultMessage();

            try {
                errorCode = ErrorCode.valueOf(enumKey);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown validation key: {}", enumKey);
            }

            attributes = extractMinMax(fieldError.getArguments());
        }

        String message = (attributes != null)
                ? mapAttribute(errorCode.getMessage(), attributes)
                : errorCode.getMessage();

        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    private Map<String, Object> extractMinMax(Object[] args) {
        if (args == null) return null;

        Integer min = null, max = null;

        for (Object a : args) {
            if (a instanceof Integer i) {
                if (min == null) min = i;
                else if (max == null) max = i;
            }
        }

        if (min == null && max == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put(MIN_ATTRIBUTE, min);
        map.put(MAX_ATTRIBUTE, max);
        return map;
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        String maxValue = String.valueOf(attributes.get(MAX_ATTRIBUTE));

        String result = message;

        if (attributes.get(MIN_ATTRIBUTE) != null) {
            result = result.replace("{" + MIN_ATTRIBUTE + "}", String.valueOf(attributes.get(MIN_ATTRIBUTE)));
        }
        if (attributes.get(MAX_ATTRIBUTE) != null) {
            result = result.replace("{" + MAX_ATTRIBUTE + "}", String.valueOf(attributes.get(MAX_ATTRIBUTE)));
        }

        return result;
    }
}
