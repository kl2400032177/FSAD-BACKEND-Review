package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.entity.User;
import com.mfinsight.enums.Role;
import com.mfinsight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    // ─── PLATFORM DASHBOARD ───────────────────────────────────

    /**
     * GET /api/admin/stats
     * High-level platform statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        return ResponseEntity.ok(ApiResponse.success(userService.getPlatformStats(), "Platform stats"));
    }

    // ─── USER MANAGEMENT ──────────────────────────────────────

    /**
     * GET /api/admin/users
     * List every registered user.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(), "All users"));
    }

    /**
     * GET /api/admin/users/{id}
     * Get one user by id.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id), "User fetched"));
    }

    /**
     * GET /api/admin/users/role/{role}
     * Filter users by role.  role = INVESTOR | FINANCIAL_ADVISOR | DATA_ANALYST | ADMIN
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(role),
                "Users with role " + role));
    }

    /**
     * PATCH /api/admin/users/{id}/toggle-status
     * Enable or disable a user account.
     */
    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<User>> toggleUserStatus(@PathVariable Long id) {
        User user = userService.toggleUserStatus(id);
        String msg = user.isActive() ? "User activated" : "User deactivated";
        return ResponseEntity.ok(ApiResponse.success(user, msg));
    }

    /**
     * DELETE /api/admin/users/{id}
     * Permanently delete a user.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", "Deleted successfully"));
    }
}
