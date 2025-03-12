package com.xalts.multiUserApproval.request;

import com.xalts.multiUserApproval.validations.ValidTaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskApprovalRequest {
  @NotNull(message = "Task ID is required")
  @Positive(message = "Task ID must be a positive number")
  private Long taskId;
  @NotBlank(message = "Status is required")
  @ValidTaskStatus(message = "Invalid Task Status")
  private String status;
  private String comment;
}
