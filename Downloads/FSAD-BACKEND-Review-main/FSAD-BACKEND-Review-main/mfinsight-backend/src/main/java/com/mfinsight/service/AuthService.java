package com.mfinsight.service;

import com.mfinsight.dto.AuthDTO;
import com.mfinsight.entity.User;
import com.mfinsight.repository.UserRepository;
import com.mfinsight.security.JwtUtils;
import com.mfinsight.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private LoginAttemptService loginAttemptService;

    public User register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .riskAppetite(request.getRiskAppetite())
                .investmentGoal(request.getInvestmentGoal())
                .monthlyIncome(request.getMonthlyIncome())
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request, String clientIp) {
        String loginKey = (request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase()) + "|" + clientIp;
        loginAttemptService.assertLoginAllowed(loginKey);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            loginAttemptService.recordFailure(loginKey);
            throw new BadCredentialsException("Invalid email or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        loginAttemptService.clear(loginKey);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthDTO.AuthResponse(jwt, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
