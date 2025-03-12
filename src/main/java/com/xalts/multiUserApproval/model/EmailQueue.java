package com.xalts.multiUserApproval.model;

import com.xalts.multiUserApproval.constants.EmailStatus;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "email_queue")
public class EmailQueue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String recipient;
  private String subject;

  @Column(columnDefinition = "TEXT")
  private String body;

  @Enumerated(EnumType.STRING)
  private EmailStatus status = EmailStatus.PENDING;

  private int retryCount = 0;

  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}

