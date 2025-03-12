package com.xalts.multiUserApproval.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xalts.multiUserApproval.model.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findTaskByCreatedBy_Id(Long id);
}
