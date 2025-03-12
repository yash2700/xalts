package com.xalts.multiUserApproval.scheduler;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.constants.EmailStatus;
import com.xalts.multiUserApproval.model.EmailQueue;
import com.xalts.multiUserApproval.repository.EmailQueueRepository;
import com.xalts.multiUserApproval.service.EmailSenderService;

import java.util.Arrays;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailProcessorService {

  private final EmailQueueRepository emailQueueRepository;
  private final EmailSenderService emailSenderService;

  public EmailProcessorService(EmailQueueRepository emailQueueRepository,
      EmailSenderService emailSenderService) {
    this.emailQueueRepository = emailQueueRepository;
    this.emailSenderService = emailSenderService;
  }

  @Scheduled(cron = "0 */5 * * * *") // Runs every 5 minutes
  public void processPendingEmails() {
    log.info("📧 Fetching pending and failed emails...");

    int pageSize = 1000;
    PageRequest pageRequest = PageRequest.of(0, pageSize);
    Page<EmailQueue> emailQueuePage;

    do {
      emailQueuePage = emailQueueRepository.findByStatusInAndRetryCountLessThan(
          Arrays.asList(EmailStatus.PENDING, EmailStatus.FAILED),
          3, // Max retry attempts before giving up
          pageRequest
      );

      List<EmailQueue> pendingEmails = emailQueuePage.getContent();

      if (pendingEmails.isEmpty()) {
        log.info("📭 No pending emails found.");
        return;
      }

      log.info("📤 Processing {} emails...", pendingEmails.size());

      // Process emails asynchronously
      pendingEmails.forEach(emailSenderService::sendEmailAsync);

      pageRequest = pageRequest.next();
    } while (emailQueuePage.hasNext());

    log.info("✅ Email processing completed.");
  }

}


