package com.mfinsight.service;

import com.mfinsight.dto.MutualFundDTO;
import com.mfinsight.entity.MutualFund;
import com.mfinsight.enums.FundCategory;
import com.mfinsight.enums.RiskLevel;
import com.mfinsight.repository.MutualFundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MutualFundService {

    @Autowired
    private MutualFundRepository mutualFundRepository;

    public MutualFund createFund(MutualFundDTO.CreateRequest request) {
        MutualFund fund = MutualFund.builder()
                .fundName(request.getFundName())
                .amcName(request.getAmcName())
                .category(request.getCategory())
                .riskLevel(request.getRiskLevel())
                .nav(request.getNav())
                .expenseRatio(request.getExpenseRatio())
                .oneYearReturn(request.getOneYearReturn())
                .threeYearReturn(request.getThreeYearReturn())
                .fiveYearReturn(request.getFiveYearReturn())
                .aum(request.getAum())
                .minimumInvestment(request.getMinimumInvestment())
                .minimumSip(request.getMinimumSip())
                .description(request.getDescription())
                .inceptionDate(request.getInceptionDate())
                .active(true)
                .build();
        return mutualFundRepository.save(fund);
    }

    public MutualFund updateFund(Long fundId, MutualFundDTO.UpdateRequest request) {
        MutualFund fund = getFundById(fundId);

        if (request.getFundName() != null) fund.setFundName(request.getFundName());
        if (request.getCategory() != null) fund.setCategory(request.getCategory());
        if (request.getRiskLevel() != null) fund.setRiskLevel(request.getRiskLevel());
        if (request.getNav() != null) fund.setNav(request.getNav());
        if (request.getExpenseRatio() != null) fund.setExpenseRatio(request.getExpenseRatio());
        if (request.getOneYearReturn() != null) fund.setOneYearReturn(request.getOneYearReturn());
        if (request.getThreeYearReturn() != null) fund.setThreeYearReturn(request.getThreeYearReturn());
        if (request.getFiveYearReturn() != null) fund.setFiveYearReturn(request.getFiveYearReturn());
        if (request.getAum() != null) fund.setAum(request.getAum());
        if (request.getMinimumInvestment() != null) fund.setMinimumInvestment(request.getMinimumInvestment());
        if (request.getMinimumSip() != null) fund.setMinimumSip(request.getMinimumSip());
        if (request.getDescription() != null) fund.setDescription(request.getDescription());
        if (request.getActive() != null) fund.setActive(request.getActive());

        return mutualFundRepository.save(fund);
    }

    public MutualFund getFundById(Long id) {
        return mutualFundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mutual Fund not found with id: " + id));
    }

    public List<MutualFund> getAllActiveFunds() {
        return mutualFundRepository.findByActive(true);
    }

    public List<MutualFund> getAllFunds() {
        return mutualFundRepository.findAll();
    }

    public List<MutualFund> getFundsByCategory(FundCategory category) {
        return mutualFundRepository.findByCategory(category);
    }

    public List<MutualFund> getFundsByRiskLevel(RiskLevel riskLevel) {
        return mutualFundRepository.findByRiskLevel(riskLevel);
    }

    public List<MutualFund> searchFunds(String keyword) {
        return mutualFundRepository.searchByKeyword(keyword);
    }

    public List<MutualFund> getTopPerformers() {
        return mutualFundRepository.findTopPerformersByOneYear();
    }

    public List<MutualFund> compareFunds(List<Long> fundIds) {
        return fundIds.stream()
                .map(this::getFundById)
                .collect(Collectors.toList());
    }

    public List<MutualFund> recommendFunds(RiskLevel riskLevel, FundCategory category) {
        if (riskLevel != null && category != null) {
            return mutualFundRepository.findByCategoryAndRiskLevel(category, riskLevel);
        } else if (riskLevel != null) {
            return mutualFundRepository.findByRiskLevel(riskLevel);
        } else if (category != null) {
            return mutualFundRepository.findByCategory(category);
        }
        return mutualFundRepository.findByActive(true);
    }

    public void deleteFund(Long id) {
        MutualFund fund = getFundById(id);
        fund.setActive(false);
        mutualFundRepository.save(fund);
    }

    public MutualFundDTO.Response toResponse(MutualFund fund) {
        MutualFundDTO.Response response = new MutualFundDTO.Response();
        response.setId(fund.getId());
        response.setFundName(fund.getFundName());
        response.setAmcName(fund.getAmcName());
        response.setCategory(fund.getCategory());
        response.setRiskLevel(fund.getRiskLevel());
        response.setNav(fund.getNav());
        response.setExpenseRatio(fund.getExpenseRatio());
        response.setOneYearReturn(fund.getOneYearReturn());
        response.setThreeYearReturn(fund.getThreeYearReturn());
        response.setFiveYearReturn(fund.getFiveYearReturn());
        response.setAum(fund.getAum());
        response.setMinimumInvestment(fund.getMinimumInvestment());
        response.setMinimumSip(fund.getMinimumSip());
        response.setDescription(fund.getDescription());
        response.setInceptionDate(fund.getInceptionDate());
        response.setActive(fund.isActive());
        return response;
    }
}
