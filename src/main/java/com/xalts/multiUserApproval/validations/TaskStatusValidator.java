package com.xalts.multiUserApproval.validations;


import com.xalts.multiUserApproval.constants.TaskStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskStatusValidator implements ConstraintValidator<ValidTaskStatus, String> {

  @Override
  public void initialize(ValidTaskStatus constraintAnnotation) {
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return false; // or return true if null should be allowed
    }
    try {
      TaskStatus taskStatus = TaskStatus.fromValue(value);
      return taskStatus != null; // checks if it's a valid enum value
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
