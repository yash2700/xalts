package com.xalts.multiUserApproval.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.xalts.multiUserApproval.repository.UserRepository;

import java.util.ArrayList;

@Service
public class UserDetailService implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
        .map(user -> new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            new ArrayList<>()
        ))
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
