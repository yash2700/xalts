package com.xalts.multiUserApproval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xalts.multiUserApproval.constants.TaskStatus;
import com.xalts.multiUserApproval.model.Task;
import com.xalts.multiUserApproval.model.TaskApproval;
import com.xalts.multiUserApproval.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskApprovalRepository extends JpaRepository<TaskApproval, Long> {

  // Find an approval record for a specific task and approver
  Optional<TaskApproval> findByTaskAndApprovedBy(Task task, User approvedBy);

  // Count the number of approvals with APPROVED status for a specific task
  long countByTaskAndStatus(Task task, TaskStatus status);

  // Get all approvals for a task
  List<TaskApproval> findByTask(Task task);

  List<TaskApproval> findByTaskAndStatus(Task task, TaskStatus taskStatus);

  List<TaskApproval> findByApprovedByEmail(String email);

}

