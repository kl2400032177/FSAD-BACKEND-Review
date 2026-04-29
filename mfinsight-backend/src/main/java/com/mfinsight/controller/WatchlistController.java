package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.dto.MutualFundDTO;
import com.mfinsight.service.MutualFundService;
import com.mfinsight.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = "*")
public class WatchlistController {

    @Autowired private WatchlistService watchlistService;
    @Autowired private MutualFundService mutualFundService;

    @PostMapping("/{fundId}")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<String>> addToWatchlist(@PathVariable Long fundId) {
        watchlistService.addToWatchlist(fundId);
        return ResponseEntity.ok(ApiResponse.success("Fund added to watchlist", "Added successfully"));
    }

    @DeleteMapping("/{fundId}")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<String>> removeFromWatchlist(@PathVariable Long fundId) {
        watchlistService.removeFromWatchlist(fundId);
        return ResponseEntity.ok(ApiResponse.success("Fund removed from watchlist", "Removed successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<List<MutualFundDTO.Response>>> getMyWatchlist() {
        List<MutualFundDTO.Response> funds = watchlistService.getMyWatchlist()
                .stream()
                .map(w -> mutualFundService.toResponse(w.getMutualFund()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(funds, "Watchlist fetched"));
    }

    @GetMapping("/check/{fundId}")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<ApiResponse<Boolean>> isInWatchlist(@PathVariable Long fundId) {
        boolean result = watchlistService.isInWatchlist(fundId);
        return ResponseEntity.ok(ApiResponse.success(result, "Watchlist check"));
    }
}