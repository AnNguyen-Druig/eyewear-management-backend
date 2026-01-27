package com.swp391.eyewear_management_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message Key",  HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User not exists", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1004, "Username must be at least {min} characters!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Password must be at least {min} characters!", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),          //401
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),     //403
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),         //400
    ;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private HttpStatusCode httpStatusCode;
    private int code;
    private String message;
}
