package com.xalts.multiUserApproval.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.xalts.multiUserApproval.constants.ExceptionConstants;
import com.xalts.multiUserApproval.exceptions.StandardException;
import com.xalts.multiUserApproval.model.User;
import com.xalts.multiUserApproval.repository.UserRepository;
import com.xalts.multiUserApproval.request.UserRequest;
import com.xalts.multiUserApproval.response.UserResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @InjectMocks
  UserService userService;
  @Mock
  UserRepository userRepository;
  @Mock
  BCryptPasswordEncoder passwordEncoder;
  @Mock
  TaskApprovalService taskApprovalService;
  @Mock
  TaskQueryService taskService;

  // Successfully creates a new user when email doesn't exist
  @Test
  public void test_create_user_success() {
    // Arrange
    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("test@example.com");
    userRequest.setName("Test User");
    userRequest.setPassword("password123");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

    User savedUser = User.builder()
        .id(1L)  // Ensure the ID is set correctly
        .email("test@example.com")
        .name("Test User")
        .password("encodedPassword")
        .build();

    when(userRepository.save(any(User.class))).thenReturn(savedUser);  // Mock save to return the
    // savedUser with ID set

    // Act
    UserResponse response = userService.createUser(userRequest);

    // Assert
    verify(userRepository).findByEmail("test@example.com");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));

    assertEquals("test@example.com", response.getEmail());
    assertEquals("Test User", response.getName());
  }

  // Throws StandardException when email already exists
  @Test
  public void test_create_user_throws_exception_when_email_exists() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("existing@example.com");
    userRequest.setName("Existing User");
    userRequest.setPassword("password123");

    User existingUser = User.builder()
        .id(1L)
        .email("existing@example.com")
        .name("Existing User")
        .password("encodedPassword")
        .build();

    when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

    // Act & Assert
    StandardException exception = assertThrows(StandardException.class, () -> {
      userService.createUser(userRequest);
    });

    assertEquals(ExceptionConstants.EMAIL_ALREADY_EXISTS, exception.getMessage());
    verify(userRepository).findByEmail("existing@example.com");
    verify(userRepository, never()).save(any(User.class));
  }

  // Returns UserResponse with correct id, name, and email
  @Test
  public void test_create_user_returns_correct_user_response() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("test@example.com");
    userRequest.setName("Test User");
    userRequest.setPassword("password123");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

    User savedUser = User.builder()
        .id(1L)
        .email("test@example.com")
        .name("Test User")
        .password("encodedPassword")
        .build();

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // Act
    UserResponse response = userService.createUser(userRequest);

    // Assert
    verify(userRepository).findByEmail("test@example.com");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));

    assertEquals("test@example.com", response.getEmail());
    assertEquals("Test User", response.getName());
  }

  // Properly encodes password using BCryptPasswordEncoder
  @Test
  public void test_password_encoding_with_bcrypt() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("test@example.com");
    userRequest.setName("Test User");
    userRequest.setPassword("password123");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

    // Act
    userService.createUser(userRequest);

    // Assert
    verify(passwordEncoder).encode("password123");
  }

  // Processes special characters in email, name, and password
  @Test
  public void test_create_user_with_special_characters() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("test+special@example.com");
    userRequest.setName("Test@User#");
    userRequest.setPassword("p@ssw0rd!");

    when(userRepository.findByEmail("test+special@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("p@ssw0rd!")).thenReturn("encodedSpecialPassword");

    User savedUser = User.builder()
        .id(1L)
        .email("test+special@example.com")
        .name("Test@User#")
        .password("encodedSpecialPassword")
        .build();

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // Act
    UserResponse response = userService.createUser(userRequest);

    // Assert
    verify(userRepository).findByEmail("test+special@example.com");
    verify(passwordEncoder).encode("p@ssw0rd!");
    verify(userRepository).save(any(User.class));

    assertEquals("test+special@example.com", response.getEmail());
    assertEquals("Test@User#", response.getName());
  }

  // When user doesn't exist, creates a new user with encoded password
  @Test
  public void test_update_user_creates_new_user_when_not_exists() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("test@example.com");
    userRequest.setName("Test User");
    userRequest.setPassword("password123");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

    User savedUser = User.builder()
        .id(1L)
        .email("test@example.com")
        .name("Test User")
        .password("encodedPassword")
        .build();

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // Act
    UserResponse result = userService.updateUser(userRequest);

    // Assert
    verify(userRepository).findByEmail("test@example.com");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));

    assertEquals("Test User", result.getName());
    assertEquals("test@example.com", result.getEmail());
  }

  // Returns UserResponse with id, name, and email after successful update
  @Test
  public void test_update_user_returns_user_response_after_successful_update() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("test@example.com");
    userRequest.setName("Updated User");
    userRequest.setPassword("newPassword123");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

    User updatedUser = User.builder()
        .id(1L)
        .email("test@example.com")
        .name("Updated User")
        .password("encodedNewPassword")
        .build();

    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // Act
    UserResponse result = userService.updateUser(userRequest);

    // Assert
    verify(userRepository).findByEmail("test@example.com");
    verify(passwordEncoder).encode("newPassword123");
    verify(userRepository).save(any(User.class));

    assertEquals("Updated User", result.getName());
    assertEquals("test@example.com", result.getEmail());
  }

  // Correctly maps User entity to UserResponse
  @Test
  public void test_update_user_correctly_maps_user_to_user_response() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("test@example.com");
    userRequest.setName("Test User");
    userRequest.setPassword("password123");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

    User savedUser = User.builder()
        .id(1L)
        .email("test@example.com")
        .name("Test User")
        .password("encodedPassword")
        .build();

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // Act
    UserResponse result = userService.updateUser(userRequest);

    // Assert
    verify(userRepository).findByEmail("test@example.com");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));

    assertEquals("Test User", result.getName());
    assertEquals("test@example.com", result.getEmail());
  }

  // When user with email already exists, throws StandardException with USER_NOT_FOUND message
  @Test
  public void test_update_user_throws_exception_when_user_exists() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("existing@example.com");
    userRequest.setName("Existing User");
    userRequest.setPassword("password123");

    User existingUser = User.builder()
        .id(1L)
        .email("existing@example.com")
        .name("Existing User")
        .password("encodedPassword")
        .build();

    when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

    // Act & Assert
    StandardException exception = assertThrows(StandardException.class, () -> {
      userService.updateUser(userRequest);
    });

    assertEquals(ExceptionConstants.USER_NOT_FOUND, exception.getMessage());
    verify(userRepository).findByEmail("existing@example.com");
    verify(userRepository, never()).save(any(User.class));
  }

  // Handles empty string values in userRequest fields
  @Test
  public void test_update_user_with_empty_fields() {
    // Arrange

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail("");
    userRequest.setName("");
    userRequest.setPassword("");

    when(userRepository.findByEmail("")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("")).thenReturn("");

    User savedUser = User.builder()
        .id(1L)
        .email("")
        .name("")
        .password("")
        .build();

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // Act
    UserResponse result = userService.updateUser(userRequest);

    // Assert
    verify(userRepository).findByEmail("");
    verify(passwordEncoder).encode("");
    verify(userRepository).save(any(User.class));

    assertEquals("", result.getName());
    assertEquals("", result.getEmail());
  }

  // Returns UserResponse with correct user data when valid ID is provided
  @Test
  public void test_get_user_by_id_returns_correct_user_response() {
    // Arrange
    Long userId = 1L;
    User mockUser = User.builder()
        .id(userId)
        .name("John Doe")
        .email("john@example.com")
        .password("encoded_password")
        .build();


    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));


    // Act
    UserResponse response = userService.getUserById(userId);

    // Assert
    assertEquals(userId, response.getId());
    assertEquals("John Doe", response.getName());
    assertEquals("john@example.com", response.getEmail());
  }

  // Maps User entity to UserResponse correctly with id, name, and email
  @Test
  public void test_get_user_by_id_maps_user_to_user_response() {
    // Arrange
    Long userId = 1L;
    User mockUser = User.builder()
        .id(userId)
        .name("Jane Doe")
        .email("jane@example.com")
        .password("encoded_password")
        .build();

    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    // Act
    UserResponse response = userService.getUserById(userId);

    // Assert
    assertEquals(userId, response.getId());
    assertEquals("Jane Doe", response.getName());
    assertEquals("jane@example.com", response.getEmail());
  }

  // Throws RuntimeException when user with given ID is not found
  @Test
  public void test_get_user_by_id_throws_exception_when_user_not_found() {
    // Arrange
    Long nonExistentUserId = 999L;
    Mockito.when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      userService.getUserById(nonExistentUserId);
    });

    assertEquals(ExceptionConstants.USER_NOT_FOUND, exception.getMessage());
  }

  // Handles null ID parameter
  @Test
  public void test_get_user_by_id_with_null_id_throws_exception() {
    // Act & Assert
    assertThrows(RuntimeException.class, () -> {
      userService.getUserById(null);
    });
  }

  // Returns a user when a valid email is provided and user exists
  @Test
  public void test_get_user_by_email_when_user_exists() {
    // Arrange

    String email = "test@example.com";
    User expectedUser = User.builder()
        .id(1L)
        .email(email)
        .name("Test User")
        .build();

    Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

    // Act
    Optional<User> result = userService.getUserByEmail(email);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(expectedUser, result.get());
    Mockito.verify(userRepository).findByEmail(email);
  }

  // Returns an empty Optional when email doesn't exist in database
  @Test
  public void test_get_user_by_email_when_email_does_not_exist() {
    // Arrange

    String email = "nonexistent@example.com";

    Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act
    Optional<User> result = userService.getUserByEmail(email);

    // Assert
    assertFalse(result.isPresent());
    Mockito.verify(userRepository).findByEmail(email);
  }

  // Handles null email parameter
  @Test
  public void test_get_user_by_email_with_null_email() {
    // Arrange

    String email = null;
    Mockito.when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

    // Act
    Optional<User> result = userService.getUserByEmail(email);

    // Assert
    assertFalse(result.isPresent());
    Mockito.verify(userRepository).findByEmail(null);
  }

  // Handles empty string email parameter
  @Test
  public void test_get_user_by_email_with_empty_string() {
    // Arrange

    String email = "";

    Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act
    Optional<User> result = userService.getUserByEmail(email);

    // Assert
    assertFalse(result.isPresent());
    Mockito.verify(userRepository).findByEmail(email);
  }

  // Returns a list of all users when database contains users
  @Test
  public void test_get_all_users_returns_user_list() {
    List<User> userList = Arrays.asList(
        User.builder().id(1L).name("John Doe").email("john@example.com").build(),
        User.builder().id(2L).name("Jane Smith").email("jane@example.com").build()
    );

    when(userRepository.findAll()).thenReturn(userList);

    // Act
    List<UserResponse> result = userService.getAllUsers();

    // Assert
    assertEquals(2, result.size());
    assertEquals("John Doe", result.get(0).getName());
    assertEquals("jane@example.com", result.get(1).getEmail());
    verify(userRepository, times(1)).findAll();
  }

  // Maps each User entity to UserResponse correctly
  @Test
  public void test_map_user_to_userresponse() {
    // Arrange

    List<User> userList = Arrays.asList(
        User.builder().id(1L).name("Alice").email("alice@example.com").build(),
        User.builder().id(2L).name("Bob").email("bob@example.com").build()
    );

    when(userRepository.findAll()).thenReturn(userList);

    // Act
    List<UserResponse> result = userService.getAllUsers();

    // Assert
    assertEquals(2, result.size());
    assertEquals("Alice", result.get(0).getName());
    assertEquals("alice@example.com", result.get(0).getEmail());
    assertEquals("Bob", result.get(1).getName());
    assertEquals("bob@example.com", result.get(1).getEmail());
    verify(userRepository, times(1)).findAll();
  }

  // Handles large number of users efficiently
  @Test
  public void test_get_all_users_handles_large_dataset() {
    // Arrange

    List<User> largeUserList = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      largeUserList.add(User.builder()
          .id((long) i)
          .name("User " + i)
          .email("user" + i + "@example.com")
          .build());
    }

    when(userRepository.findAll()).thenReturn(largeUserList);

    // Act
    long startTime = System.currentTimeMillis();
    List<UserResponse> result = userService.getAllUsers();
    long endTime = System.currentTimeMillis();

    // Assert
    assertEquals(1000, result.size());
    assertTrue((endTime - startTime) < 1000, "Method execution took too long");
    verify(userRepository, times(1)).findAll();
  }
  // Behaves correctly when userRepository.findAll() returns null
  @Test
  public void test_get_all_users_when_find_all_returns_null() {
    // Arrange

    when(userRepository.findAll()).thenReturn(null);

    // Act
    List<UserResponse> result = userService.getAllUsers();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRepository, times(1)).findAll();
  }

}
