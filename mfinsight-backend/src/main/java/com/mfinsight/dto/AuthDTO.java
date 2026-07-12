package com.mfinsight.dto;

import com.mfinsight.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String fullName;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotNull
        private Role role;

        // Optional investor fields
        private Double riskAppetite;
        private String investmentGoal;
        private Double monthlyIncome;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private Long userId;
        private String fullName;
        private String email;
        private Role role;

        public AuthResponse(String token, Long userId, String fullName, String email, Role role) {
            this.token = token;
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }
    }
}
