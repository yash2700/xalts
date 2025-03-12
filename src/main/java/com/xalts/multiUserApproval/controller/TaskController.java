package com.xalts.multiUserApproval.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xalts.multiUserApproval.request.TaskApprovalRequest;
import com.xalts.multiUserApproval.request.TaskRequest;
import com.xalts.multiUserApproval.response.TaskResponse;
import com.xalts.multiUserApproval.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping("/create")
  public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest request,
      @AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(taskService.createTask(request, user.getUsername()));
  }

  @PostMapping("/{taskId}/approve")
  public ResponseEntity<TaskResponse> approveTask(
      @AuthenticationPrincipal UserDetails user, @RequestBody @Valid TaskApprovalRequest request) {
    return ResponseEntity.ok(taskService.approveTask(request.getTaskId(), user.getUsername(),
        request.getStatus(),
        request.getComment()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getTaskById(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.getTaskById(id));
  }
}
