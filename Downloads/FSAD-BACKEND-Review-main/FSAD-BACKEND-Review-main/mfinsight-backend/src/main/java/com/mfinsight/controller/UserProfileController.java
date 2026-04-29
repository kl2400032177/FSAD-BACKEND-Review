package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.entity.User;
import com.mfinsight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/profile
     * Any authenticated user can view their own profile.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<User>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile(), "Profile fetched"));
    }

    /**
     * PATCH /api/profile
     * Update profile fields (fullName, riskAppetite, investmentGoal, monthlyIncome).
     * Body example: { "fullName": "Alice", "riskAppetite": 7.5, "investmentGoal": "Retirement" }
     */
    @PatchMapping
    public ResponseEntity<ApiResponse<User>> updateProfile(@RequestBody Map<String, Object> updates) {
        User updated = userService.updateProfile(updates);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated"));
    }
}
