package com.xalts.multiUserApproval.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xalts.multiUserApproval.constants.EmailStatus;
import com.xalts.multiUserApproval.model.EmailQueue;

import java.util.List;

@Repository
public interface EmailQueueRepository extends JpaRepository<EmailQueue, Long> {

  Page<EmailQueue> findByStatusInAndRetryCountLessThan(
      List<EmailStatus> statuses, int maxRetry, Pageable pageable);
}

