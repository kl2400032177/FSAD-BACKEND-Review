package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.entity.Investment;
import com.mfinsight.entity.MutualFund;
import com.mfinsight.enums.FundCategory;
import com.mfinsight.enums.RiskLevel;
import com.mfinsight.repository.InvestmentRepository;
import com.mfinsight.repository.MutualFundRepository;
import com.mfinsight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analyst/reports")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('DATA_ANALYST', 'ADMIN')")
public class AnalystReportController {

    @Autowired private MutualFundRepository mutualFundRepository;
    @Autowired private InvestmentRepository investmentRepository;
    @Autowired private UserRepository userRepository;

    /**
     * GET /api/analyst/reports/fund-summary
     * High-level stats on all mutual funds.
     */
    @GetMapping("/fund-summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFundSummary() {
        List<MutualFund> allFunds = mutualFundRepository.findAll();
        long totalFunds     = allFunds.size();
        long activeFunds    = allFunds.stream().filter(MutualFund::isActive).count();

        // Average 1-year return across all active funds
        OptionalDouble avgReturn = allFunds.stream()
                .filter(f -> f.isActive() && f.getOneYearReturn() != null)
                .mapToDouble(MutualFund::getOneYearReturn)
                .average();

        // Breakdown by category
        Map<FundCategory, Long> byCategory = allFunds.stream()
                .filter(MutualFund::isActive)
                .collect(Collectors.groupingBy(MutualFund::getCategory, Collectors.counting()));

        // Breakdown by risk level
        Map<RiskLevel, Long> byRisk = allFunds.stream()
                .filter(MutualFund::isActive)
                .collect(Collectors.groupingBy(MutualFund::getRiskLevel, Collectors.counting()));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalFunds", totalFunds);
        report.put("activeFunds", activeFunds);
        report.put("inactiveFunds", totalFunds - activeFunds);
        report.put("averageOneYearReturn", avgReturn.isPresent() ? String.format("%.2f", avgReturn.getAsDouble()) : "N/A");
        report.put("fundsByCategory", byCategory);
        report.put("fundsByRiskLevel", byRisk);

        return ResponseEntity.ok(ApiResponse.success(report, "Fund summary report"));
    }

    /**
     * GET /api/analyst/reports/investment-trends
     * Platform-wide investment trend analysis.
     */
    @GetMapping("/investment-trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInvestmentTrends() {
        List<Investment> allInvestments = investmentRepository.findAll();

        long totalInvestments  = allInvestments.size();
        long activeInvestments = allInvestments.stream().filter(i -> "ACTIVE".equals(i.getStatus())).count();
        long redeemedCount     = allInvestments.stream().filter(i -> "REDEEMED".equals(i.getStatus())).count();
        long sipCount          = allInvestments.stream().filter(Investment::isSip).count();

        double totalAUM = allInvestments.stream()
                .filter(i -> "ACTIVE".equals(i.getStatus()))
                .mapToDouble(Investment::getInvestedAmount)
                .sum();

        // Top 5 most invested funds
        Map<String, Double> fundWiseAUM = allInvestments.stream()
                .filter(i -> "ACTIVE".equals(i.getStatus()))
                .collect(Collectors.groupingBy(
                        i -> i.getMutualFund().getFundName(),
                        Collectors.summingDouble(Investment::getInvestedAmount)));

        List<Map.Entry<String, Double>> top5 = fundWiseAUM.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Investment by category
        Map<String, Double> categoryWiseAUM = allInvestments.stream()
                .filter(i -> "ACTIVE".equals(i.getStatus()))
                .collect(Collectors.groupingBy(
                        i -> i.getMutualFund().getCategory().name(),
                        Collectors.summingDouble(Investment::getInvestedAmount)));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalInvestments", totalInvestments);
        report.put("activeInvestments", activeInvestments);
        report.put("redeemedInvestments", redeemedCount);
        report.put("activeSips", sipCount);
        report.put("totalAUMInvested", String.format("%.2f", totalAUM));
        report.put("top5FundsByAUM", top5.stream().collect(
                LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), String.format("%.2f", e.getValue())),
                Map::putAll));
        report.put("aumByCategoryBreakdown", categoryWiseAUM);

        return ResponseEntity.ok(ApiResponse.success(report, "Investment trends report"));
    }

    /**
     * GET /api/analyst/reports/top-performers?limit=10
     * Ranked list of best-performing funds by 1-year return.
     */
    @GetMapping("/top-performers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopPerformers(
            @RequestParam(defaultValue = "10") int limit) {

        List<Map<String, Object>> performers = mutualFundRepository
                .findTopPerformersByOneYear()
                .stream()
                .filter(f -> f.isActive() && f.getOneYearReturn() != null)
                .limit(limit)
                .map(f -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", f.getId());
                    m.put("fundName", f.getFundName());
                    m.put("amcName", f.getAmcName());
                    m.put("category", f.getCategory());
                    m.put("riskLevel", f.getRiskLevel());
                    m.put("nav", f.getNav());
                    m.put("oneYearReturn", f.getOneYearReturn());
                    m.put("threeYearReturn", f.getThreeYearReturn());
                    m.put("fiveYearReturn", f.getFiveYearReturn());
                    m.put("aum", f.getAum());
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(performers, "Top " + limit + " performing funds"));
    }

    /**
     * GET /api/analyst/reports/user-activity
     * Summary of user activity on the platform.
     */
    @GetMapping("/user-activity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserActivity() {
        long totalUsers    = userRepository.count();
        long activeUsers   = userRepository.findByActive(true).size();
        long totalInvestors = userRepository.findByRole(com.mfinsight.enums.Role.INVESTOR).size();
        long totalAdvisors  = userRepository.findByRole(com.mfinsight.enums.Role.FINANCIAL_ADVISOR).size();
        long totalAnalysts  = userRepository.findByRole(com.mfinsight.enums.Role.DATA_ANALYST).size();

        // Users with at least one active investment
        long investingUsers = investmentRepository.findAll().stream()
                .filter(i -> "ACTIVE".equals(i.getStatus()))
                .map(i -> i.getUser().getId())
                .distinct()
                .count();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalUsers", totalUsers);
        report.put("activeUsers", activeUsers);
        report.put("inactiveUsers", totalUsers - activeUsers);
        report.put("totalInvestors", totalInvestors);
        report.put("totalFinancialAdvisors", totalAdvisors);
        report.put("totalDataAnalysts", totalAnalysts);
        report.put("usersWithActiveInvestments", investingUsers);

        return ResponseEntity.ok(ApiResponse.success(report, "User activity report"));
    }

    /**
     * GET /api/analyst/reports/risk-distribution
     * How investors' money is distributed across risk levels.
     */
    @GetMapping("/risk-distribution")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRiskDistribution() {
        List<Investment> active = investmentRepository.findAll().stream()
                .filter(i -> "ACTIVE".equals(i.getStatus()))
                .collect(Collectors.toList());

        Map<String, Double> riskAUM = active.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getMutualFund().getRiskLevel().name(),
                        Collectors.summingDouble(Investment::getInvestedAmount)));

        double total = riskAUM.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, String> riskPercent = new LinkedHashMap<>();
        riskAUM.forEach((risk, amt) ->
                riskPercent.put(risk, String.format("%.2f%%", total > 0 ? (amt / total) * 100 : 0)));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("aumByRiskLevel", riskAUM);
        report.put("percentageByRiskLevel", riskPercent);
        report.put("totalActiveAUM", String.format("%.2f", total));

        return ResponseEntity.ok(ApiResponse.success(report, "Risk distribution report"));
    }
}
