package com.swp391.eyewear_management_backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {

    private int min;

    //khởi tạo các thông số của Annotation
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    //hàm xử lý kiểm tra xem data nhận vào có đúng hay không
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext constraintValidatorContext) {

        if(Objects.isNull(value)){
            return true;
        }

        long years = ChronoUnit.YEARS.between(value, LocalDate.now());

        return years >= min;
    }
}
