package com.xalts.multiUserApproval.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email is not valid")
  private String email;
  @NotBlank(message = "Password is required")
  private String password;
}
