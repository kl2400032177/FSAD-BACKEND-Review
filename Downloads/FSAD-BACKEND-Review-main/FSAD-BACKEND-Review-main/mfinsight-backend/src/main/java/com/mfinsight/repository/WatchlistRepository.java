package com.mfinsight.repository;

import com.mfinsight.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserId(Long userId);

    Optional<Watchlist> findByUserIdAndMutualFundId(Long userId, Long fundId);

    boolean existsByUserIdAndMutualFundId(Long userId, Long fundId);

    void deleteByUserIdAndMutualFundId(Long userId, Long fundId);
}
