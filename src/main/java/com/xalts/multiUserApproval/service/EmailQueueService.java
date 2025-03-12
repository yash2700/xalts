package com.xalts.multiUserApproval.service;

import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.constants.EmailStatus;
import com.xalts.multiUserApproval.model.EmailQueue;
import com.xalts.multiUserApproval.repository.EmailQueueRepository;

@Service
public class EmailQueueService {
  private final EmailQueueRepository emailQueueRepository;

  public EmailQueueService(EmailQueueRepository emailQueueRepository) {
    this.emailQueueRepository = emailQueueRepository;
  }

  public void queueEmail(String recipient, String subject, String body) {
    EmailQueue emailQueue = new EmailQueue();
    emailQueue.setRecipient(recipient);
    emailQueue.setSubject(subject);
    emailQueue.setBody(body);
    emailQueue.setStatus(EmailStatus.PENDING);

    emailQueueRepository.save(emailQueue);
  }
}
