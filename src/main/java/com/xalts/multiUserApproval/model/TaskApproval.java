package com.xalts.multiUserApproval.model;

import com.xalts.multiUserApproval.constants.TaskStatus;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "task_approvals")
public class TaskApproval {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Task task;

  @ManyToOne
  private User approvedBy;
  @Enumerated(EnumType.STRING)
  private TaskStatus status = TaskStatus.PENDING;

  private String comment;

  private LocalDateTime approvalTime;
}

