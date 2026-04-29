package com.mfinsight.repository;

import com.mfinsight.entity.FundPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FundPerformanceRepository extends JpaRepository<FundPerformance, Long> {

    List<FundPerformance> findByMutualFundIdOrderByRecordDateDesc(Long fundId);

    @Query("SELECT fp FROM FundPerformance fp WHERE fp.mutualFund.id = :fundId " +
           "AND fp.recordDate BETWEEN :startDate AND :endDate ORDER BY fp.recordDate ASC")
    List<FundPerformance> findByFundIdAndDateRange(@Param("fundId") Long fundId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    Optional<FundPerformance> findTopByMutualFundIdOrderByRecordDateDesc(Long fundId);
}
