package com.mfinsight.service;

import com.mfinsight.entity.MutualFund;
import com.mfinsight.entity.User;
import com.mfinsight.entity.Watchlist;
import com.mfinsight.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    @Autowired private WatchlistRepository watchlistRepository;
    @Autowired private MutualFundService mutualFundService;
    @Autowired private AuthService authService;

    @Transactional
    public Watchlist addToWatchlist(Long fundId) {
        User currentUser = authService.getCurrentUser();
        MutualFund fund = mutualFundService.getFundById(fundId);

        if (watchlistRepository.existsByUserIdAndMutualFundId(currentUser.getId(), fundId)) {
            throw new RuntimeException("Fund already in watchlist");
        }

        Watchlist watchlist = Watchlist.builder()
                .user(currentUser)
                .mutualFund(fund)
                .build();

        return watchlistRepository.save(watchlist);
    }

    @Transactional
    public void removeFromWatchlist(Long fundId) {
        User currentUser = authService.getCurrentUser();
        watchlistRepository.deleteByUserIdAndMutualFundId(currentUser.getId(), fundId);
    }

    public List<Watchlist> getMyWatchlist() {
        User currentUser = authService.getCurrentUser();
        return watchlistRepository.findByUserId(currentUser.getId());
    }

    public boolean isInWatchlist(Long fundId) {
        User currentUser = authService.getCurrentUser();
        return watchlistRepository.existsByUserIdAndMutualFundId(currentUser.getId(), fundId);
    }
}
