package com.xalts.multiUserApproval.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailStatus {
  PENDING("PENDING"),
  SENT("SENT"),
  FAILED("FAILED");

  private final String value;
}
