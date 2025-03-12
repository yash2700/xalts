package com.xalts.multiUserApproval.validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@jakarta.validation.Constraint(validatedBy = TaskStatusValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaskStatus {
  String message() default "Invalid TaskStatus value";

  Class<?>[] groups() default {};

  Class<? extends jakarta.validation.Payload>[] payload() default {};
}

