package com.xalts.multiUserApproval.response;

import com.xalts.multiUserApproval.constants.TaskStatus;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
  private Long id;
  private String description;
  private TaskStatus status;
  private List<String> approvedEmails;
  private List<String> approvers;
}
