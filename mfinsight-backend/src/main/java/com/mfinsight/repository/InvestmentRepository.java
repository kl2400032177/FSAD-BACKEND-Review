package com.mfinsight.repository;

import com.mfinsight.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserId(Long userId);

    List<Investment> findByMutualFundId(Long fundId);

    List<Investment> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT i FROM Investment i WHERE i.user.id = :userId AND i.isSip = true AND i.status = 'ACTIVE'")
    List<Investment> findActiveSipsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(i.investedAmount) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    Double getTotalInvestedAmountByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT i.mutualFund.id) FROM Investment i WHERE i.user.id = :userId AND i.status = 'ACTIVE'")
    Long countDistinctFundsByUser(@Param("userId") Long userId);

    @Query("SELECT i.mutualFund.category, SUM(i.investedAmount) FROM Investment i " +
           "WHERE i.user.id = :userId AND i.status = 'ACTIVE' GROUP BY i.mutualFund.category")
    List<Object[]> getPortfolioAllocationByUser(@Param("userId") Long userId);
}
