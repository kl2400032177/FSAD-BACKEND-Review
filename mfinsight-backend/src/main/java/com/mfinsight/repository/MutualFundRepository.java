package com.mfinsight.repository;

import com.mfinsight.entity.MutualFund;
import com.mfinsight.enums.FundCategory;
import com.mfinsight.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {

    List<MutualFund> findByCategory(FundCategory category);

    List<MutualFund> findByRiskLevel(RiskLevel riskLevel);

    List<MutualFund> findByAmcName(String amcName);

    List<MutualFund> findByActive(boolean active);

    @Query("SELECT m FROM MutualFund m WHERE " +
           "LOWER(m.fundName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.amcName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MutualFund> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT m FROM MutualFund m WHERE m.category = :category AND m.riskLevel = :riskLevel AND m.active = true")
    List<MutualFund> findByCategoryAndRiskLevel(@Param("category") FundCategory category,
                                                 @Param("riskLevel") RiskLevel riskLevel);

    @Query("SELECT m FROM MutualFund m ORDER BY m.oneYearReturn DESC")
    List<MutualFund> findTopPerformersByOneYear();

    @Query("SELECT m FROM MutualFund m WHERE m.active = true ORDER BY m.aum DESC")
    List<MutualFund> findTopByAum();
}
