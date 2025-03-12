package com.xalts.multiUserApproval.service;

import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.model.Task;
import com.xalts.multiUserApproval.repository.TaskApprovalRepository;
import com.xalts.multiUserApproval.repository.TaskRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskQueryService {

  private final TaskRepository taskRepository;
  private final TaskApprovalRepository taskApprovalRepository;

  public TaskQueryService(TaskRepository taskRepository, TaskApprovalRepository taskApprovalRepository) {
    this.taskRepository = taskRepository;
    this.taskApprovalRepository = taskApprovalRepository;
  }

  // Fetch tasks grouped by status where the user is the creator
  public Map<String, List<String>> getCreatedTasksBy(Long id) {
    List<Task> createdTasks = taskRepository.findTaskByCreatedBy_Id(id);

    return createdTasks.stream()
        .collect(Collectors.groupingBy(
            task -> task.getStatus().getValue(),
            Collectors.mapping(
                task -> "Task #" + task.getId() + ": " + task.getDescription(),
                Collectors.toList()
            )
        ));
  }
}

