package com.xalts.multiUserApproval.model;

import com.xalts.multiUserApproval.constants.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tasks")
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private User createdBy;

  private String description;

  @Enumerated(EnumType.STRING)
  private TaskStatus status = TaskStatus.PENDING;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  private List<TaskApproval> approvals = new ArrayList<>();

  @ElementCollection
  private List<String> approverEmails = new ArrayList<>();
}

