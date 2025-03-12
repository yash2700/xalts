package com.xalts.multiUserApproval.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xalts.multiUserApproval.constants.ExceptionConstants;
import com.xalts.multiUserApproval.exceptions.StandardException;
import com.xalts.multiUserApproval.model.User;
import com.xalts.multiUserApproval.repository.UserRepository;
import com.xalts.multiUserApproval.request.UserRequest;
import com.xalts.multiUserApproval.response.UserResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final TaskApprovalService taskApprovalService;
  private final TaskQueryService taskService;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
      TaskApprovalService taskApprovalService, TaskQueryService taskService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.taskApprovalService = taskApprovalService;
    this.taskService = taskService;
  }

  public UserResponse createUser(UserRequest userRequest) {
    Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
    if (existingUser.isPresent()) {
      throw new StandardException(ExceptionConstants.EMAIL_ALREADY_EXISTS);
    }

    User user = User.builder().email(userRequest.getEmail()).name(userRequest.getName())
        .password(passwordEncoder.encode(userRequest.getPassword())).build();
    userRepository.save(user);
    return getUserWithApprovedTasks(user);
  }

  public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new StandardException(ExceptionConstants.USER_NOT_FOUND));
    return getUserWithApprovedTasks(user);
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public List<UserResponse> getAllUsers() {
    List<User> users = userRepository.findAll();
    if (CollectionUtils.isEmpty(users)) {
      return Collections.emptyList();
    }
    return users.stream().map(this::getUserWithApprovedTasks).toList();
  }

  public UserResponse updateUser(UserRequest userRequest) {
    Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
    if (existingUser.isPresent()) {
      throw new StandardException(ExceptionConstants.USER_NOT_FOUND);
    }
    User user = User.builder().email(userRequest.getEmail()).name(userRequest.getName())
        .password(passwordEncoder.encode(userRequest.getPassword())).build();
    userRepository.save(user);
    return getUserWithApprovedTasks(user);
  }

  public UserResponse getUserWithApprovedTasks(User user) {
    Map<String, List<String>> approvedTasks = taskApprovalService.getApprovedTasksByUser(user.getEmail());
    Map<String, List<String>> createdTasksByStatus = taskService
        .getCreatedTasksBy(user.getId());
    return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .approvedTasks(approvedTasks)
        .createdTasksByStatus(createdTasksByStatus)
        .build();
  }
}
