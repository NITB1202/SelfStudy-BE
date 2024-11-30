package com.example.selfstudybe.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            LocalDateTime startDate = (LocalDateTime) getFieldValue(value, "startDate");
            LocalDateTime endDate = (LocalDateTime) getFieldValue(value, "endDate");

            if (startDate == null || endDate == null) {
                return true;
            }

            return startDate.isBefore(endDate);
        } catch (Exception e) {
            return false;
        }
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}



