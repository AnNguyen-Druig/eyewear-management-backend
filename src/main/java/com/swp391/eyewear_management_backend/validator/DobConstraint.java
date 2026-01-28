package com.swp391.eyewear_management_backend.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target({FIELD})    //dùng cho kiểu gì: METHOD, FIELD, CONSTRUCTOR,...
@Retention(RetentionPolicy.RUNTIME)     //sẽ đc xử lý lúc nào: RUNTIME, ...
@Constraint(validatedBy = { DobValidator.class })
public @interface DobConstraint {

    String message() default "Invalid date of birth";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
