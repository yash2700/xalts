package com.xalts.multiUserApproval.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
  private Long id;
  private String name;
  private String email;
  private Map<String, List<String>> approvedTasks;
  private Map<String, List<String>> createdTasksByStatus; // Tasks created by user
}
