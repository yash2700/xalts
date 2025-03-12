package com.xalts.multiUserApproval.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.config.JwtUtil;
import com.xalts.multiUserApproval.repository.UserRepository;
import com.xalts.multiUserApproval.response.LoginResponse;

@Service
public class AuthService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final UserDetailService userDetailsService;

  public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil,
      AuthenticationManager authenticationManager, UserDetailService userDetailsService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
  }

  public LoginResponse authenticate(String email, String password) {
    try {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setToken(jwtUtil.generateToken(userDetails));
    return loginResponse;
    } catch (Exception e) {
      LOGGER.error("Authentication failed: {}", e.getMessage());
      return null;
    }
  }
}
