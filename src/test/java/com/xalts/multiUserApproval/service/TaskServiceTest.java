package com.xalts.multiUserApproval.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xalts.multiUserApproval.constants.ExceptionConstants;
import com.xalts.multiUserApproval.constants.TaskStatus;
import com.xalts.multiUserApproval.exceptions.StandardException;
import com.xalts.multiUserApproval.model.Task;
import com.xalts.multiUserApproval.model.User;
import com.xalts.multiUserApproval.repository.TaskApprovalRepository;
import com.xalts.multiUserApproval.repository.TaskRepository;
import com.xalts.multiUserApproval.request.TaskRequest;
import com.xalts.multiUserApproval.response.TaskResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
  @InjectMocks
  TaskService taskService;
  @Mock
  UserService userService;
  @Mock
  TaskRepository taskRepository;
  @Mock
  TaskApprovalRepository taskApprovalRepository;
  @Mock
  EmailQueueService emailQueueService;

  // Creates a task with valid request and creator email
  @Test
  public void test_create_task_with_valid_request_and_creator() {
    String creatorEmail = "creator@example.com";
    User creator = User.builder().id(1L).email(creatorEmail).name("Creator").build();

    List<String> approverEmails = Arrays.asList("approver1@example.com", "approver2@example.com");
    TaskRequest request = new TaskRequest();
    request.setDescription("Test Task");
    request.setApproverEmails(approverEmails);

    User approver1 = User.builder().id(2L).email("approver1@example.com").name("Approver 1")
        .build();
    User approver2 = User.builder().id(3L).email("approver2@example.com").name("Approver 2")
        .build();

    Task savedTask = new Task();
    savedTask.setId(1L);
    savedTask.setDescription("Test Task");
    savedTask.setCreatedBy(creator);
    savedTask.setApproverEmails(approverEmails);
    savedTask.setStatus(TaskStatus.PENDING);

    // Mock behavior
    when(userService.getUserByEmail(creatorEmail)).thenReturn(Optional.of(creator));
    when(userService.getUserByEmail("approver1@example.com")).thenReturn(Optional.of(approver1));
    when(userService.getUserByEmail("approver2@example.com")).thenReturn(Optional.of(approver2));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
      Task task = invocation.getArgument(0);
      task.setId(1L);
      return task;
    });

    // Act
    TaskResponse response = taskService.createTask(request, creatorEmail);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("Test Task", response.getDescription());
    assertEquals(TaskStatus.PENDING, response.getStatus());
    assertEquals(approverEmails, response.getApprovers());

    verify(taskRepository).save(any(Task.class));
    verify(taskApprovalRepository).saveAll(anyList());
    verify(emailQueueService, times(2)).queueEmail(anyString(), eq("Task Approval Request"),
        anyString());
  }

  // Sets task description, creator, approver emails, and status correctly
  @Test
  public void test_create_task_sets_description_creator_approvers_and_status() {
    String creatorEmail = "creator@example.com";
    User creator = User.builder().id(1L).email(creatorEmail).name("Creator").build();

    List<String> approverEmails = Arrays.asList("approver1@example.com", "approver2@example.com");
    TaskRequest request = new TaskRequest();
    request.setDescription("Test Task");
    request.setApproverEmails(approverEmails);

    User approver1 = User.builder().id(2L).email("approver1@example.com").name("Approver 1")
        .build();
    User approver2 = User.builder().id(3L).email("approver2@example.com").name("Approver 2")
        .build();

    Task savedTask = new Task();
    savedTask.setId(1L);
    savedTask.setDescription("Test Task");
    savedTask.setCreatedBy(creator);
    savedTask.setApproverEmails(approverEmails);
    savedTask.setStatus(TaskStatus.PENDING);

    // Mock behavior
    when(userService.getUserByEmail(creatorEmail)).thenReturn(Optional.of(creator));
    when(userService.getUserByEmail("approver1@example.com")).thenReturn(Optional.of(approver1));
    when(userService.getUserByEmail("approver2@example.com")).thenReturn(Optional.of(approver2));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
      Task task = invocation.getArgument(0);
      task.setId(1L);
      return task;
    });

    // Act
    TaskResponse response = taskService.createTask(request, creatorEmail);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("Test Task", response.getDescription());
    assertEquals(TaskStatus.PENDING, response.getStatus());
    assertEquals(approverEmails, response.getApprovers());

    verify(taskRepository).save(any(Task.class));
    verify(taskApprovalRepository).saveAll(anyList());
    verify(emailQueueService, times(2)).queueEmail(anyString(), eq("Task Approval Request"),
        anyString());
  }

  // Creates TaskApproval records for each approver
  @Test
  public void test_creates_task_approval_records_for_each_approver() {

    String creatorEmail = "creator@example.com";
    User creator = User.builder().id(1L).email(creatorEmail).name("Creator").build();

    List<String> approverEmails = Arrays.asList("approver1@example.com", "approver2@example.com");
    TaskRequest request = new TaskRequest();
    request.setDescription("Test Task");
    request.setApproverEmails(approverEmails);

    User approver1 = User.builder().id(2L).email("approver1@example.com").name("Approver 1")
        .build();
    User approver2 = User.builder().id(3L).email("approver2@example.com").name("Approver 2")
        .build();

    Task savedTask = new Task();
    savedTask.setId(1L);
    savedTask.setDescription("Test Task");
    savedTask.setCreatedBy(creator);
    savedTask.setApproverEmails(approverEmails);
    savedTask.setStatus(TaskStatus.PENDING);

    // Mock behavior
    when(userService.getUserByEmail(creatorEmail)).thenReturn(Optional.of(creator));
    when(userService.getUserByEmail("approver1@example.com")).thenReturn(Optional.of(approver1));
    when(userService.getUserByEmail("approver2@example.com")).thenReturn(Optional.of(approver2));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
      Task task = invocation.getArgument(0);
      task.setId(1L);
      return task;
    });

    // Act
    TaskResponse response = taskService.createTask(request, creatorEmail);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("Test Task", response.getDescription());
    assertEquals(TaskStatus.PENDING, response.getStatus());
    assertEquals(approverEmails, response.getApprovers());

    verify(taskRepository).save(any(Task.class));
    verify(taskApprovalRepository).saveAll(anyList());
    verify(emailQueueService, times(2)).queueEmail(anyString(), eq("Task Approval Request"),
        anyString());
  }

  // Creator email not found in the system
  @Test
  public void test_create_task_with_nonexistent_creator() {
    String nonExistentCreatorEmail = "nonexistent@example.com";

    List<String> approverEmails = Arrays.asList("approver1@example.com", "approver2@example.com");
    TaskRequest request = new TaskRequest();
    request.setDescription("Test Task");
    request.setApproverEmails(approverEmails);

    // Mock behavior
    when(userService.getUserByEmail(nonExistentCreatorEmail)).thenReturn(Optional.empty());

    // Act & Assert
    StandardException exception = assertThrows(StandardException.class, () -> {
      taskService.createTask(request, nonExistentCreatorEmail);
    });

    assertEquals(ExceptionConstants.USER_NOT_FOUND, exception.getMessage());

    verify(userService).getUserByEmail(nonExistentCreatorEmail);
    verifyNoInteractions(taskRepository);
    verifyNoInteractions(taskApprovalRepository);
    verifyNoInteractions(emailQueueService);
  }

  // Creator attempts to add themselves as an approver
  @Test
  public void test_creator_cannot_be_approver() {
    String creatorEmail = "creator@example.com";
    User creator = User.builder().id(1L).email(creatorEmail).name("Creator").build();

    List<String> approverEmails = Arrays.asList("creator@example.com", "approver2@example.com");
    TaskRequest request = new TaskRequest();
    request.setDescription("Test Task");
    request.setApproverEmails(approverEmails);

    // Mock behavior
    when(userService.getUserByEmail(creatorEmail)).thenReturn(Optional.of(creator));

    // Act & Assert
    StandardException exception = assertThrows(StandardException.class, () -> {
      taskService.createTask(request, creatorEmail);
    });

    assertEquals(ExceptionConstants.CREATOR_NOT_APPROVER, exception.getMessage());

    verify(taskRepository, never()).save(any(Task.class));
    verify(taskApprovalRepository, never()).saveAll(anyList());
    verify(emailQueueService, never()).queueEmail(anyString(), anyString(), anyString());
  }

}
