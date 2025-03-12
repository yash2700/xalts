package com.xalts.multiUserApproval.service;

import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.constants.TaskStatus;
import com.xalts.multiUserApproval.model.TaskApproval;
import com.xalts.multiUserApproval.repository.TaskApprovalRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskApprovalService {
  private final TaskApprovalRepository taskApprovalRepository;

  public TaskApprovalService(TaskApprovalRepository taskApprovalRepository) {
    this.taskApprovalRepository = taskApprovalRepository;
  }
  public Map<String, List<String>> getApprovedTasksByUser(String email) {
    List<TaskApproval> approvals = taskApprovalRepository.findByApprovedByEmail(email);

    return approvals.stream()
        .collect(Collectors.groupingBy(
            ta -> ta.getStatus().getValue(),  // Group by status (APPROVED, PENDING, REJECTED)
            Collectors.mapping(
                ta -> "Task #" + ta.getTask().getId() + ": " + ta.getTask().getDescription(), // Format task details
                Collectors.toList()
            )
        ));
  }
}
