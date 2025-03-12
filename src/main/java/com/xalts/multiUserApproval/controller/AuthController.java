package com.xalts.multiUserApproval.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xalts.multiUserApproval.exceptions.ErrorResponse;
import com.xalts.multiUserApproval.request.LoginRequest;
import com.xalts.multiUserApproval.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
    try {
      return new ResponseEntity<>(authService.authenticate(loginRequest.getEmail(),
          loginRequest.getPassword()),
          HttpStatus.OK);
    } catch (Exception e) {
      com.xalts.multiUserApproval.exceptions.ErrorResponse errorResponse = ErrorResponse.builder()
          .message("Invalid credentials")
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .build();
      return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }
  }
}
