package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.dto.FundPerformanceDTO;
import com.mfinsight.entity.FundPerformance;
import com.mfinsight.service.FundPerformanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analyst/performance")
@CrossOrigin(origins = "*")
public class FundPerformanceController {

    @Autowired
    private FundPerformanceService performanceService;

    /**
     * POST /api/analyst/performance
     * Data Analyst uploads a NAV / performance record for a fund.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DATA_ANALYST', 'ADMIN')")
    public ResponseEntity<ApiResponse<FundPerformanceDTO.Response>> addRecord(
            @Valid @RequestBody FundPerformanceDTO.CreateRequest request) {
        FundPerformance record = performanceService.addPerformanceRecord(request);
        return ResponseEntity.ok(ApiResponse.success(
                performanceService.toResponse(record), "Performance record added"));
    }

    /**
     * GET /api/analyst/performance/{fundId}/history
     * Full NAV history for a fund (most recent first).
     */
    @GetMapping("/{fundId}/history")
    public ResponseEntity<ApiResponse<List<FundPerformanceDTO.Response>>> getHistory(
            @PathVariable Long fundId) {
        List<FundPerformanceDTO.Response> history = performanceService.getPerformanceHistory(fundId)
                .stream().map(performanceService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(history, "Performance history for fund " + fundId));
    }

    /**
     * GET /api/analyst/performance/{fundId}/range?start=2024-01-01&end=2024-12-31
     * NAV data filtered by date range — useful for charting.
     */
    @GetMapping("/{fundId}/range")
    public ResponseEntity<ApiResponse<List<FundPerformanceDTO.Response>>> getByDateRange(
            @PathVariable Long fundId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<FundPerformanceDTO.Response> records =
                performanceService.getPerformanceByDateRange(fundId, start, end)
                        .stream().map(performanceService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(records, "Performance data for range"));
    }
}
