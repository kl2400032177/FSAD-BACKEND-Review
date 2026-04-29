package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.dto.MutualFundDTO;
import com.mfinsight.entity.MutualFund;
import com.mfinsight.enums.FundCategory;
import com.mfinsight.enums.RiskLevel;
import com.mfinsight.service.MutualFundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/funds")
@CrossOrigin(origins = "*")
public class MutualFundController {

    @Autowired
    private MutualFundService mutualFundService;

    // ─── PUBLIC ENDPOINTS ─────────────────────────────────────

    /**
     * GET /api/funds/public/all
     * List all active funds (no login required).
     */
    @GetMapping("/public/all")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> getAllPublicFunds() {
        List<MutualFundDTO.Response> funds = mutualFundService.getAllActiveFunds()
                .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(funds, "Funds fetched successfully"));
    }

    /**
     * GET /api/funds/public/{id}
     * Get details of a single fund (no login required).
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<MutualFundDTO.Response>> getPublicFundById(@PathVariable Long id) {
        MutualFund fund = mutualFundService.getFundById(id);
        return ResponseEntity.ok(ApiResponse.success(mutualFundService.toResponse(fund), "Fund fetched"));
    }

    /**
     * GET /api/funds/public/search?keyword=xyz
     */
    @GetMapping("/public/search")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> searchFunds(@RequestParam String keyword) {
        List<MutualFundDTO.Response> funds = mutualFundService.searchFunds(keyword)
                .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(funds, "Search results"));
    }

    /**
     * GET /api/funds/public/top-performers
     */
    @GetMapping("/public/top-performers")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> getTopPerformers() {
        List<MutualFundDTO.Response> funds = mutualFundService.getTopPerformers()
                .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(funds, "Top performers"));
    }

    // ─── AUTHENTICATED ENDPOINTS ──────────────────────────────

    /**
     * GET /api/funds/filter?category=EQUITY&riskLevel=HIGH
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> filterFunds(
            @RequestParam(required = false) FundCategory category,
            @RequestParam(required = false) RiskLevel riskLevel) {
        List<MutualFundDTO.Response> funds;
        if (category != null && riskLevel != null) {
            funds = mutualFundService.recommendFunds(riskLevel, category)
                    .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        } else if (category != null) {
            funds = mutualFundService.getFundsByCategory(category)
                    .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        } else if (riskLevel != null) {
            funds = mutualFundService.getFundsByRiskLevel(riskLevel)
                    .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        } else {
            funds = mutualFundService.getAllActiveFunds()
                    .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        }
        return ResponseEntity.ok(ApiResponse.success(funds, "Filtered funds"));
    }

    /**
     * POST /api/funds/compare
     * Body: { "fundIds": [1, 2, 3] }
     */
    @PostMapping("/compare")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> compareFunds(
            @RequestBody List<Long> fundIds) {
        List<MutualFundDTO.Response> funds = mutualFundService.compareFunds(fundIds)
                .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(funds, "Comparison data"));
    }

    /**
     * GET /api/funds/recommend?riskLevel=HIGH&category=EQUITY
     */
    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> recommendFunds(
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) FundCategory category) {
        List<MutualFundDTO.Response> funds = mutualFundService.recommendFunds(riskLevel, category)
                .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(funds, "Recommended funds"));
    }

    // ─── MANAGE (DATA_ANALYST / ADMIN) ────────────────────────

    /**
     * POST /api/funds/manage
     * Create a new mutual fund.
     */
    @PostMapping("/manage")
    @PreAuthorize("hasAnyRole('DATA_ANALYST', 'ADMIN')")
    public ResponseEntity<ApiResponse<MutualFundDTO.Response>> createFund(
            @Valid @RequestBody MutualFundDTO.CreateRequest request) {
        MutualFund fund = mutualFundService.createFund(request);
        return ResponseEntity.ok(ApiResponse.success(mutualFundService.toResponse(fund), "Fund created successfully"));
    }

    /**
     * PUT /api/funds/manage/{id}
     * Update fund details.
     */
    @PutMapping("/manage/{id}")
    @PreAuthorize("hasAnyRole('DATA_ANALYST', 'ADMIN')")
    public ResponseEntity<ApiResponse<MutualFundDTO.Response>> updateFund(
            @PathVariable Long id,
            @RequestBody MutualFundDTO.UpdateRequest request) {
        MutualFund fund = mutualFundService.updateFund(id, request);
        return ResponseEntity.ok(ApiResponse.success(mutualFundService.toResponse(fund), "Fund updated successfully"));
    }

    /**
     * DELETE /api/funds/manage/{id}
     * Soft-delete a fund (set active=false).
     */
    @DeleteMapping("/manage/{id}")
    @PreAuthorize("hasAnyRole('DATA_ANALYST', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteFund(@PathVariable Long id) {
        mutualFundService.deleteFund(id);
        return ResponseEntity.ok(ApiResponse.success("Fund deactivated", "Fund deleted successfully"));
    }

    /**
     * GET /api/funds/manage/all
     * Get all funds including inactive (admin/analyst only).
     */
    @GetMapping("/manage/all")
    @PreAuthorize("hasAnyRole('DATA_ANALYST', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> getAllFundsAdmin() {
        List<MutualFundDTO.Response> funds = mutualFundService.getAllFunds()
                .stream().map(mutualFundService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(funds, "All funds (including inactive)"));
    }
}
