package com.syfe.pfm;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

record RegisterRequest(@NotBlank @Email String username,@NotBlank @Size(min=8,max=100) String password,@NotBlank @Size(max=120) String fullName,@NotBlank @Pattern(regexp="^[+0-9() .-]{7,20}$") String phoneNumber) {}
record LoginRequest(@NotBlank @Email String username,@NotBlank String password) {}
record TransactionRequest(@NotNull @DecimalMin(value="0.01") BigDecimal amount,@NotNull @PastOrPresent LocalDate date,@NotBlank String category,@Size(max=500) String description) {}
record CategoryRequest(@NotBlank @Size(max=60) String name,@NotNull TransactionType type) {}
record GoalRequest(@NotBlank @Size(max=120) String goalName,@NotNull @DecimalMin(value="0.01") BigDecimal targetAmount,@NotNull @Future LocalDate targetDate) {}
record GoalUpdateRequest(@NotNull @DecimalMin(value="0.01") BigDecimal targetAmount,@NotNull @Future LocalDate targetDate) {}
record MessageResponse(String message) {}
record UserResponse(Long id,String username,String fullName,String phoneNumber) {}
record CategoryResponse(Long id,String name,TransactionType type,boolean isCustom) {}
record TransactionResponse(Long id,BigDecimal amount,LocalDate date,String category,String description,TransactionType type) {}
record GoalResponse(Long id,String goalName,BigDecimal targetAmount,LocalDate targetDate,LocalDate startDate,BigDecimal currentProgress,BigDecimal progressPercentage,BigDecimal remainingAmount) {}
