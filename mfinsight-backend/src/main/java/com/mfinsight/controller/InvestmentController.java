package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.dto.InvestmentDTO;
import com.mfinsight.entity.Investment;
import com.mfinsight.service.InvestmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/investments")
@CrossOrigin(origins = "*")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    /**
     * POST /api/investments
     * Invest in a mutual fund (Investor role).
     */
    @PostMapping
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<InvestmentDTO.Response>> invest(
            @Valid @RequestBody InvestmentDTO.CreateRequest request) {
        Investment investment = investmentService.invest(request);
        return ResponseEntity.ok(ApiResponse.success(
                investmentService.toResponse(investment), "Investment created successfully"));
    }

    /**
     * POST /api/investments/redeem
     * Redeem an investment.
     */
    @PostMapping("/redeem")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<InvestmentDTO.Response>> redeem(
            @Valid @RequestBody InvestmentDTO.RedeemRequest request) {
        Investment investment = investmentService.redeemInvestment(request);
        return ResponseEntity.ok(ApiResponse.success(
                investmentService.toResponse(investment), "Investment redeemed successfully"));
    }

    /**
     * GET /api/investments/my
     * Get all investments of the logged-in investor.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<List<InvestmentDTO.Response>>> getMyInvestments() {
        List<InvestmentDTO.Response> investments = investmentService.getMyInvestments()
                .stream().map(investmentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(investments, "Your investments"));
    }

    /**
     * GET /api/investments/portfolio
     * Get portfolio summary of current investor.
     */
    @GetMapping("/portfolio")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<InvestmentDTO.PortfolioSummary>> getPortfolio() {
        InvestmentDTO.PortfolioSummary summary = investmentService.getPortfolioSummary();
        return ResponseEntity.ok(ApiResponse.success(summary, "Portfolio summary"));
    }

    /**
     * GET /api/investments/admin/user/{userId}
     * Admin: View investments of any user.
     */
    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<InvestmentDTO.Response>>> getUserInvestments(
            @PathVariable Long userId) {
        List<InvestmentDTO.Response> investments = investmentService.getInvestmentsByUser(userId)
                .stream().map(investmentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(investments, "Investments for user " + userId));
    }

    /**
     * GET /api/investments/admin/fund/{fundId}
     * Admin / Analyst: View all investments in a specific fund.
     */
    @GetMapping("/admin/fund/{fundId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DATA_ANALYST')")
    public ResponseEntity<ApiResponse<List<InvestmentDTO.Response>>> getFundInvestments(
            @PathVariable Long fundId) {
        List<InvestmentDTO.Response> investments = investmentService.getInvestmentsByFund(fundId)
                .stream().map(investmentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(investments, "Investments for fund " + fundId));
    }
}
