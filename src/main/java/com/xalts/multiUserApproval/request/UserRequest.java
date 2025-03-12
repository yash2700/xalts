package com.xalts.multiUserApproval.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
  @Email(message = "Email is not valid")
  @NotBlank(message = "Email is required")
  private String email;
  @NotBlank(message = "Name is required")
  @Size(min = 6, max = 50, message = "Name must be between 6 and 50 characters")
  private String name;
  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
  private String password;
}
