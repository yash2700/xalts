package com.xalts.multiUserApproval.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.xalts.multiUserApproval.model.User;
import com.xalts.multiUserApproval.repository.TaskApprovalRepository;
import com.xalts.multiUserApproval.repository.TaskRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class TaskQueryServiceTest {
  @InjectMocks
  TaskQueryService taskQueryService;

  @Mock
  TaskRepository taskRepository;

  @Mock
  TaskApprovalRepository taskApprovalRepository;

  // Returns tasks grouped by status for a valid user ID
  @Test
  public void test_returns_tasks_grouped_by_status_for_valid_user_id() {
    Long userId = 1L;
    User user = new User();
    user.setId(userId);

    Task pendingTask = new Task();
    pendingTask.setId(1L);
    pendingTask.setCreatedBy(user);
    pendingTask.setDescription("Pending task");
    pendingTask.setStatus(TaskStatus.PENDING);

    Task approvedTask = new Task();
    approvedTask.setId(2L);
    approvedTask.setCreatedBy(user);
    approvedTask.setDescription("Approved task");
    approvedTask.setStatus(TaskStatus.APPROVED);

    List<Task> tasks = Arrays.asList(pendingTask, approvedTask);

    when(taskRepository.findTaskByCreatedBy_Id(userId)).thenReturn(tasks);

    // Act
    Map<String, List<String>> result = taskQueryService.getCreatedTasksBy(userId);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.containsKey("PENDING"));
    assertTrue(result.containsKey("APPROVED"));
    assertEquals(1, result.get("PENDING").size());
    assertEquals(1, result.get("APPROVED").size());
    assertEquals("Task #1: Pending task", result.get("PENDING").get(0));
    assertEquals("Task #2: Approved task", result.get("APPROVED").get(0));

    verify(taskRepository).findTaskByCreatedBy_Id(userId);
  }

  // Maps each task to a formatted string with ID and description
  @Test
  public void test_maps_tasks_to_formatted_string_with_id_and_description() {
    // Arrange
    Long userId = 1L;

    TaskQueryService taskQueryService = new TaskQueryService(taskRepository, taskApprovalRepository);

    User user = new User();
    user.setId(userId);

    Task task1 = new Task();
    task1.setId(1L);
    task1.setCreatedBy(user);
    task1.setDescription("First task");
    task1.setStatus(TaskStatus.PENDING);

    Task task2 = new Task();
    task2.setId(2L);
    task2.setCreatedBy(user);
    task2.setDescription("Second task");
    task2.setStatus(TaskStatus.APPROVED);

    List<Task> tasks = Arrays.asList(task1, task2);

    when(taskRepository.findTaskByCreatedBy_Id(userId)).thenReturn(tasks);

    // Act
    Map<String, List<String>> result = taskQueryService.getCreatedTasksBy(userId);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.containsKey("PENDING"));
    assertTrue(result.containsKey("APPROVED"));
    assertEquals(1, result.get("PENDING").size());
    assertEquals(1, result.get("APPROVED").size());
    assertEquals("Task #1: First task", result.get("PENDING").get(0));
    assertEquals("Task #2: Second task", result.get("APPROVED").get(0));

    verify(taskRepository).findTaskByCreatedBy_Id(userId);
  }

  // Groups tasks correctly by their status values (PENDING, APPROVED, REJECTED)
  @Test
  public void test_groups_tasks_by_status_correctly() {
    // Arrange
    Long userId = 1L;
    User user = new User();
    user.setId(userId);

    Task pendingTask = new Task();
    pendingTask.setId(1L);
    pendingTask.setCreatedBy(user);
    pendingTask.setDescription("Pending task");
    pendingTask.setStatus(TaskStatus.PENDING);

    Task approvedTask = new Task();
    approvedTask.setId(2L);
    approvedTask.setCreatedBy(user);
    approvedTask.setDescription("Approved task");
    approvedTask.setStatus(TaskStatus.APPROVED);

    Task rejectedTask = new Task();
    rejectedTask.setId(3L);
    rejectedTask.setCreatedBy(user);
    rejectedTask.setDescription("Rejected task");
    rejectedTask.setStatus(TaskStatus.REJECTED);

    List<Task> tasks = Arrays.asList(pendingTask, approvedTask, rejectedTask);

    when(taskRepository.findTaskByCreatedBy_Id(userId)).thenReturn(tasks);

    // Act
    Map<String, List<String>> result = taskQueryService.getCreatedTasksBy(userId);

    // Assert
    assertEquals(3, result.size());
    assertTrue(result.containsKey("PENDING"));
    assertTrue(result.containsKey("APPROVED"));
    assertTrue(result.containsKey("REJECTED"));
    assertEquals(1, result.get("PENDING").size());
    assertEquals(1, result.get("APPROVED").size());
    assertEquals(1, result.get("REJECTED").size());
    assertEquals("Task #1: Pending task", result.get("PENDING").get(0));
    assertEquals("Task #2: Approved task", result.get("APPROVED").get(0));
    assertEquals("Task #3: Rejected task", result.get("REJECTED").get(0));

    verify(taskRepository).findTaskByCreatedBy_Id(userId);
  }

  // Handles null user ID
  @Test
  public void test_handles_null_user_id() {
    when(taskRepository.findTaskByCreatedBy_Id(null)).thenThrow(new IllegalArgumentException("User ID cannot be null"));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      taskQueryService.getCreatedTasksBy(null);
    });

    assertEquals("User ID cannot be null", exception.getMessage());
    verify(taskRepository).findTaskByCreatedBy_Id(null);
  }

  // Handles non-existent user ID
  @Test
  public void test_handles_non_existent_user_id() {
    // Arrange
    Long nonExistentUserId = 999L;
    when(taskRepository.findTaskByCreatedBy_Id(nonExistentUserId)).thenReturn(Collections.emptyList());

    // Act
    Map<String, List<String>> result = taskQueryService.getCreatedTasksBy(nonExistentUserId);

    // Assert
    assertTrue(result.isEmpty());

    verify(taskRepository).findTaskByCreatedBy_Id(nonExistentUserId);
  }

  // Handles tasks with null description
  @Test
  public void test_handles_tasks_with_null_description() {
    Long userId = 1L;
    User user = new User();
    user.setId(userId);

    Task taskWithNullDescription = new Task();
    taskWithNullDescription.setId(1L);
    taskWithNullDescription.setCreatedBy(user);
    taskWithNullDescription.setDescription(null);
    taskWithNullDescription.setStatus(TaskStatus.PENDING);

    List<Task> tasks = Arrays.asList(taskWithNullDescription);

    when(taskRepository.findTaskByCreatedBy_Id(userId)).thenReturn(tasks);

    // Act
    Map<String, List<String>> result = taskQueryService.getCreatedTasksBy(userId);

    // Assert
    assertEquals(1, result.size());
    assertTrue(result.containsKey("PENDING"));
    assertEquals(1, result.get("PENDING").size());
    assertEquals("Task #1: null", result.get("PENDING").get(0));

    verify(taskRepository).findTaskByCreatedBy_Id(userId);
  }
}
