package com.xalts.multiUserApproval.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xalts.multiUserApproval.constants.TaskStatus;
import com.xalts.multiUserApproval.model.Task;
import com.xalts.multiUserApproval.model.TaskApproval;
import com.xalts.multiUserApproval.repository.TaskApprovalRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class TaskApprovalServiceTest {
  @InjectMocks
  TaskApprovalService taskApprovalService;
  @Mock
  TaskApprovalRepository taskApprovalRepository;

  // Returns a map of task descriptions grouped by status for a user with multiple approvals
  @Test
  public void test_returns_map_of_tasks_grouped_by_status() {
    // Arrange
    String email = "user@example.com";

    Task task1 = new Task();
    task1.setId(1L);
    task1.setDescription("First task");

    Task task2 = new Task();
    task2.setId(2L);
    task2.setDescription("Second task");

    Task task3 = new Task();
    task3.setId(3L);
    task3.setDescription("Third task");

    TaskApproval approval1 = TaskApproval.builder()
        .task(task1)
        .status(TaskStatus.APPROVED)
        .build();

    TaskApproval approval2 = TaskApproval.builder()
        .task(task2)
        .status(TaskStatus.PENDING)
        .build();

    TaskApproval approval3 = TaskApproval.builder()
        .task(task3)
        .status(TaskStatus.APPROVED)
        .build();

    List<TaskApproval> approvals = Arrays.asList(approval1, approval2, approval3);

    when(taskApprovalRepository.findByApprovedByEmail(email)).thenReturn(approvals);

    // Act
    Map<String, List<String>> result = taskApprovalService.getApprovedTasksByUser(email);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.containsKey(TaskStatus.APPROVED.getValue()));
    assertTrue(result.containsKey(TaskStatus.PENDING.getValue()));
    assertEquals(2, result.get(TaskStatus.APPROVED.getValue()).size());
    assertEquals(1, result.get(TaskStatus.PENDING.getValue()).size());
    assertTrue(result.get(TaskStatus.APPROVED.getValue()).contains("Task #1: First task"));
    assertTrue(result.get(TaskStatus.APPROVED.getValue()).contains("Task #3: Third task"));
    assertTrue(result.get(TaskStatus.PENDING.getValue()).contains("Task #2: Second task"));
  }

  // Returns a map with keys matching TaskStatus values (PENDING, APPROVED, REJECTED)
  @Test
  public void test_get_approved_tasks_by_user_groups_tasks_by_status() {
    String email = "user@example.com";
    Task task1 = new Task();
    task1.setId(1L);
    task1.setDescription("First task");

    Task task2 = new Task();
    task2.setId(2L);
    task2.setDescription("Second task");

    Task task3 = new Task();
    task3.setId(3L);
    task3.setDescription("Third task");

    TaskApproval approval1 = TaskApproval.builder()
        .task(task1)
        .status(TaskStatus.APPROVED)
        .build();

    TaskApproval approval2 = TaskApproval.builder()
        .task(task2)
        .status(TaskStatus.PENDING)
        .build();

    TaskApproval approval3 = TaskApproval.builder()
        .task(task3)
        .status(TaskStatus.REJECTED)
        .build();

    List<TaskApproval> approvals = Arrays.asList(approval1, approval2, approval3);

    when(taskApprovalRepository.findByApprovedByEmail(email)).thenReturn(approvals);

    // Act
    Map<String, List<String>> result = taskApprovalService.getApprovedTasksByUser(email);

    // Assert
    assertEquals(3, result.size());
    assertTrue(result.containsKey(TaskStatus.APPROVED.getValue()));
    assertTrue(result.containsKey(TaskStatus.PENDING.getValue()));
    assertTrue(result.containsKey(TaskStatus.REJECTED.getValue()));
    assertEquals(1, result.get(TaskStatus.APPROVED.getValue()).size());
    assertEquals(1, result.get(TaskStatus.PENDING.getValue()).size());
    assertEquals(1, result.get(TaskStatus.REJECTED.getValue()).size());
    assertTrue(result.get(TaskStatus.APPROVED.getValue()).contains("Task #1: First task"));
    assertTrue(result.get(TaskStatus.PENDING.getValue()).contains("Task #2: Second task"));
    assertTrue(result.get(TaskStatus.REJECTED.getValue()).contains("Task #3: Third task"));
  }

  // Each task description is formatted as "Task
  @Test
  public void test_task_description_formatting() {
    String email = "user@example.com";
    Task task1 = new Task();
    task1.setId(1L);
    task1.setDescription("First task");

    Task task2 = new Task();
    task2.setId(2L);
    task2.setDescription("Second task");

    TaskApproval approval1 = TaskApproval.builder()
        .task(task1)
        .status(TaskStatus.APPROVED)
        .build();

    TaskApproval approval2 = TaskApproval.builder()
        .task(task2)
        .status(TaskStatus.REJECTED)
        .build();

    List<TaskApproval> approvals = Arrays.asList(approval1, approval2);

    when(taskApprovalRepository.findByApprovedByEmail(email)).thenReturn(approvals);

    // Act
    Map<String, List<String>> result = taskApprovalService.getApprovedTasksByUser(email);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.get(TaskStatus.APPROVED.getValue()).contains("Task #1: First task"));
    assertTrue(result.get(TaskStatus.REJECTED.getValue()).contains("Task #2: Second task"));
  }

  // Handles null email parameter
  @Test
  public void test_handles_null_email_parameter() {
    // Arrange
    String email = null;
    when(taskApprovalRepository.findByApprovedByEmail(email)).thenReturn(Collections.emptyList());

    // Act
    Map<String, List<String>> result = taskApprovalService.getApprovedTasksByUser(email);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(taskApprovalRepository).findByApprovedByEmail(null);
  }

  // Handles empty email string
  @Test
  public void test_handles_empty_email_string() {
    // Arrange
    String email = "";
    when(taskApprovalRepository.findByApprovedByEmail(email)).thenReturn(Collections.emptyList());

    // Act
    Map<String, List<String>> result = taskApprovalService.getApprovedTasksByUser(email);

    // Assert
    assertTrue(result.isEmpty());
  }

  // Handles email that doesn't exist in the system
  @Test
  public void test_handles_nonexistent_email() {
    // Arrange
    String email = "nonexistent@example.com";
    when(taskApprovalRepository.findByApprovedByEmail(email)).thenReturn(Collections.emptyList());

    // Act
    Map<String, List<String>> result = taskApprovalService.getApprovedTasksByUser(email);

    // Assert
    assertTrue(result.isEmpty());
  }
}
