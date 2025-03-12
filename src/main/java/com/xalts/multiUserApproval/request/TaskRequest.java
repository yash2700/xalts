package com.xalts.multiUserApproval.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequest {
  @NotBlank(message = "Description is required")
  private String description;
  @NotEmpty(message = "Approver emails are required")
  @Size(min = 1, message = "At least one approver email is required")
  private List<String> approverEmails;
}
