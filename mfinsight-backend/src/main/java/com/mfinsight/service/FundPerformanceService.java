package com.mfinsight.service;

import com.mfinsight.dto.FundPerformanceDTO;
import com.mfinsight.entity.FundPerformance;
import com.mfinsight.entity.MutualFund;
import com.mfinsight.repository.FundPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FundPerformanceService {

    @Autowired private FundPerformanceRepository performanceRepository;
    @Autowired private MutualFundService mutualFundService;

    public FundPerformance addPerformanceRecord(FundPerformanceDTO.CreateRequest request) {
        MutualFund fund = mutualFundService.getFundById(request.getMutualFundId());

        FundPerformance performance = FundPerformance.builder()
                .mutualFund(fund)
                .recordDate(request.getRecordDate())
                .nav(request.getNav())
                .dailyReturn(request.getDailyReturn())
                .weeklyReturn(request.getWeeklyReturn())
                .monthlyReturn(request.getMonthlyReturn())
                .build();

        // Update fund's current NAV
        fund.setNav(request.getNav());
        mutualFundService.updateFund(fund.getId(), buildNavUpdate(request.getNav()));

        return performanceRepository.save(performance);
    }

    private com.mfinsight.dto.MutualFundDTO.UpdateRequest buildNavUpdate(Double nav) {
        com.mfinsight.dto.MutualFundDTO.UpdateRequest req = new com.mfinsight.dto.MutualFundDTO.UpdateRequest();
        req.setNav(nav);
        return req;
    }

    public List<FundPerformance> getPerformanceHistory(Long fundId) {
        return performanceRepository.findByMutualFundIdOrderByRecordDateDesc(fundId);
    }

    public List<FundPerformance> getPerformanceByDateRange(Long fundId, LocalDate start, LocalDate end) {
        return performanceRepository.findByFundIdAndDateRange(fundId, start, end);
    }

    public FundPerformanceDTO.Response toResponse(FundPerformance fp) {
        FundPerformanceDTO.Response res = new FundPerformanceDTO.Response();
        res.setId(fp.getId());
        res.setMutualFundId(fp.getMutualFund().getId());
        res.setFundName(fp.getMutualFund().getFundName());
        res.setRecordDate(fp.getRecordDate());
        res.setNav(fp.getNav());
        res.setDailyReturn(fp.getDailyReturn());
        res.setWeeklyReturn(fp.getWeeklyReturn());
        res.setMonthlyReturn(fp.getMonthlyReturn());
        return res;
    }
}
