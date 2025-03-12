package com.xalts.multiUserApproval.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.constants.EmailStatus;
import com.xalts.multiUserApproval.model.EmailQueue;
import com.xalts.multiUserApproval.repository.EmailQueueRepository;

import java.time.LocalDateTime;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

  private final EmailQueueRepository emailQueueRepository;
  private final JavaMailSender mailSender;

  @Async("emailExecutor")
  public void sendEmailAsync(EmailQueue emailQueue) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setTo(emailQueue.getRecipient());
      helper.setSubject(emailQueue.getSubject());
      helper.setText(emailQueue.getBody(), true);

      mailSender.send(message);
      log.info("Email sent to {}", emailQueue.getRecipient());

      updateEmailStatus(emailQueue, EmailStatus.SENT);
    } catch (Exception ex) {
      log.error("Failed to send email to {}: {}", emailQueue.getRecipient(), ex.getMessage());

      updateEmailStatus(emailQueue, EmailStatus.FAILED);
    }
  }

  @Transactional
  public void updateEmailStatus(EmailQueue emailQueue, EmailStatus status) {
    emailQueue.setStatus(status);
    emailQueue.setUpdatedAt(LocalDateTime.now());

    if (status == EmailStatus.FAILED) {
      emailQueue.setRetryCount(emailQueue.getRetryCount() + 1);
    }

    emailQueueRepository.save(emailQueue);
  }
}

