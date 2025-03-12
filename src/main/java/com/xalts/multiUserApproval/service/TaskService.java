package com.xalts.multiUserApproval.service;

import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.constants.ExceptionConstants;
import com.xalts.multiUserApproval.constants.TaskStatus;
import com.xalts.multiUserApproval.exceptions.StandardException;
import com.xalts.multiUserApproval.model.Task;
import com.xalts.multiUserApproval.model.TaskApproval;
import com.xalts.multiUserApproval.model.User;
import com.xalts.multiUserApproval.repository.TaskApprovalRepository;
import com.xalts.multiUserApproval.repository.TaskRepository;
import com.xalts.multiUserApproval.request.TaskRequest;
import com.xalts.multiUserApproval.response.TaskResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
public class TaskService {
  private final TaskRepository taskRepository;
  private final UserService userService;
  private final TaskApprovalRepository taskApprovalRepository;
  private final EmailQueueService emailQueueService;

  public TaskService(TaskRepository taskRepository, UserService userService,
      TaskApprovalRepository taskApprovalRepository, EmailQueueService emailQueueService) {
    this.taskRepository = taskRepository;
    this.userService = userService;
    this.taskApprovalRepository = taskApprovalRepository;
    this.emailQueueService = emailQueueService;
  }

  @Transactional
  public TaskResponse createTask(TaskRequest request, String creatorEmail) {
    User creator = userService.getUserByEmail(creatorEmail)
        .orElseThrow(() -> new StandardException(ExceptionConstants.USER_NOT_FOUND));
    if (request.getApproverEmails().contains(creatorEmail)) {
      throw new StandardException(ExceptionConstants.CREATOR_NOT_APPROVER);
    }
    Task task = new Task();
    task.setDescription(request.getDescription());
    task.setCreatedBy(creator);
    task.setApproverEmails(request.getApproverEmails());
    task.setStatus(TaskStatus.PENDING);

    // Save task first
    taskRepository.save(task);

    // Create TaskApproval records for each selected approver
    List<TaskApproval> approvals = new ArrayList<>();
    for (String email : request.getApproverEmails()) {
      User approver = userService.getUserByEmail(email).get();

      TaskApproval approval = new TaskApproval();
      approval.setTask(task);
      approval.setApprovedBy(approver);
      approval.setStatus(TaskStatus.PENDING);
      approvals.add(approval);

      // Queue email for approval request
      emailQueueService.queueEmail(
          email,
          "Task Approval Request",
          "You have been assigned to approve Task #" + task.getId() + ": " + task.getDescription()
      );
    }

    // Save approvals
    taskApprovalRepository.saveAll(approvals);

    return mapToTaskResponse(task);
  }


  @Transactional
  public TaskResponse approveTask(Long taskId, String userEmail, String taskStatus,
      String comment) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new StandardException(ExceptionConstants.TASK_NOT_FOUND));

    User approver = userService.getUserByEmail(userEmail)
        .orElseThrow(() -> new StandardException(ExceptionConstants.USER_NOT_FOUND));

    // Find existing approval record
    TaskApproval approval = taskApprovalRepository.findByTaskAndApprovedBy(task, approver)
        .orElseThrow(() -> new StandardException(ExceptionConstants.USER_NOT_APPROVER));

    if (approval.getStatus() == TaskStatus.APPROVED) {
      throw new StandardException(ExceptionConstants.TASK_ALREADY_APPROVED);
    }

    // Update approval status
    approval.setStatus(TaskStatus.fromValue(taskStatus));
    approval.setComment(comment);
    approval.setApprovalTime(LocalDateTime.now());
    taskApprovalRepository.save(approval);

    if (TaskStatus.fromValue(taskStatus) == TaskStatus.APPROVED) {
      emailQueueService.queueEmail(
          task.getCreatedBy().getEmail(),
          "Task #" + task.getId() + " Approved",
          "Task #" + task.getId() + " has been  approved by assigned approver - " + userEmail
      );
    }

    // Get all assigned approvers' emails
    Set<String> assignedApprovers = new HashSet<>(task.getApproverEmails());

    // Get users who have approved the task
    Set<String> approvedUsers = taskApprovalRepository.findByTaskAndStatus(task,
            TaskStatus.APPROVED)
        .stream()
        .map(ta -> ta.getApprovedBy().getEmail())
        .collect(Collectors.toSet());

    // Check if all assigned approvers have approved
    boolean allApproved = assignedApprovers.equals(approvedUsers);
    if (allApproved) {
      task.setStatus(TaskStatus.APPROVED);
      taskRepository.save(task);

      // Notify the creator and approvers asynchronously
      List<String> allInvolved = new ArrayList<>(assignedApprovers);
      allInvolved.add(task.getCreatedBy().getEmail());

      for (String email : allInvolved) {
        emailQueueService.queueEmail(
            email,
            "Task #" + task.getId() + " Approved",
            "Task #" + task.getId() + " has been fully approved by all assigned approvers."
        );
      }
    }

    return mapToTaskResponse(task);
  }


  private TaskResponse mapToTaskResponse(Task task) {
    return TaskResponse.builder()
        .id(task.getId())
        .description(task.getDescription())
        .status(task.getStatus())
        .approvers(task.getApproverEmails())
        .approvedEmails(task.getApprovals().stream()
            .filter(i -> i.getStatus() == TaskStatus.APPROVED)
            .map(ta -> ta.getApprovedBy().getEmail())
            .toList())
        .build();
  }

  public TaskResponse getTaskById(Long id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new StandardException(ExceptionConstants.TASK_NOT_FOUND));
    return mapToTaskResponse(task);
  }
}
