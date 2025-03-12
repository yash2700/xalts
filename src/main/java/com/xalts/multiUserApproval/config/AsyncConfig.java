package com.xalts.multiUserApproval.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

  @Bean(name = "emailExecutor")
  public Executor emailExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);  // Number of threads running at a time
    executor.setMaxPoolSize(10);  // Maximum threads allowed
    executor.setQueueCapacity(500);  // Queue size before creating new threads
    executor.setThreadNamePrefix("EmailSender-");
    executor.initialize();
    return executor;
  }
}
