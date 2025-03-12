package com.xalts.multiUserApproval.constants;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskStatus {
  PENDING("PENDING"),
  APPROVED("APPROVED"),
  REJECTED("REJECTED");

  private final String value;

  public static TaskStatus fromValue(String value) {
    return Arrays.stream(TaskStatus.values())
        .filter(status -> status.getValue().equals(value))
        .findFirst()
        .orElse(null);
  }
}
