package com.mfinsight.service;

import com.mfinsight.entity.User;
import com.mfinsight.enums.Role;
import com.mfinsight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private AuthService authService;

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User updateProfile(Map<String, Object> updates) {
        User user = authService.getCurrentUser();

        if (updates.containsKey("fullName")) user.setFullName((String) updates.get("fullName"));
        if (updates.containsKey("riskAppetite")) user.setRiskAppetite(Double.parseDouble(updates.get("riskAppetite").toString()));
        if (updates.containsKey("investmentGoal")) user.setInvestmentGoal((String) updates.get("investmentGoal"));
        if (updates.containsKey("monthlyIncome")) user.setMonthlyIncome(Double.parseDouble(updates.get("monthlyIncome").toString()));

        return userRepository.save(user);
    }

    public User toggleUserStatus(Long userId) {
        User user = getById(userId);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalInvestors", userRepository.findByRole(Role.INVESTOR).size());
        stats.put("totalAdvisors", userRepository.findByRole(Role.FINANCIAL_ADVISOR).size());
        stats.put("totalAnalysts", userRepository.findByRole(Role.DATA_ANALYST).size());
        stats.put("activeUsers", userRepository.findByActive(true).size());
        return stats;
    }

    public User getMyProfile() {
        return authService.getCurrentUser();
    }
}
