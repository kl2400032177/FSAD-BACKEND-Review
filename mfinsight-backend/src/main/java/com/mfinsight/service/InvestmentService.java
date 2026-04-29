package com.mfinsight.service;

import com.mfinsight.dto.InvestmentDTO;
import com.mfinsight.entity.Investment;
import com.mfinsight.entity.MutualFund;
import com.mfinsight.entity.User;
import com.mfinsight.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class InvestmentService {

    @Autowired private InvestmentRepository investmentRepository;
    @Autowired private MutualFundService mutualFundService;
    @Autowired private AuthService authService;

    @Transactional
    public Investment invest(InvestmentDTO.CreateRequest request) {
        User currentUser = authService.getCurrentUser();
        MutualFund fund = mutualFundService.getFundById(request.getMutualFundId());

        double nav = fund.getNav();
        double units = request.getInvestedAmount() / nav;

        Investment investment = Investment.builder()
                .user(currentUser)
                .mutualFund(fund)
                .investedAmount(request.getInvestedAmount())
                .units(units)
                .navAtPurchase(nav)
                .purchaseDate(request.getPurchaseDate())
                .isSip(request.isSip())
                .sipAmount(request.getSipAmount())
                .sipFrequency(request.getSipFrequency())
                .status("ACTIVE")
                .build();

        return investmentRepository.save(investment);
    }

    @Transactional
    public Investment redeemInvestment(InvestmentDTO.RedeemRequest request) {
        Investment investment = investmentRepository.findById(request.getInvestmentId())
                .orElseThrow(() -> new RuntimeException("Investment not found"));

        User currentUser = authService.getCurrentUser();
        if (!investment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized: This investment does not belong to you");
        }

        MutualFund fund = investment.getMutualFund();
        double currentNav = fund.getNav();
        double redemptionAmount = investment.getUnits() * currentNav;

        investment.setStatus("REDEEMED");
        investment.setRedemptionDate(request.getRedemptionDate());
        investment.setRedemptionNav(currentNav);
        investment.setRedemptionAmount(redemptionAmount);

        return investmentRepository.save(investment);
    }

    public List<Investment> getMyInvestments() {
        User currentUser = authService.getCurrentUser();
        return investmentRepository.findByUserId(currentUser.getId());
    }

    public List<Investment> getMyActiveInvestments() {
        User currentUser = authService.getCurrentUser();
        return investmentRepository.findByUserIdAndStatus(currentUser.getId(), "ACTIVE");
    }

    public InvestmentDTO.PortfolioSummary getPortfolioSummary() {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();

        List<Investment> activeInvestments = investmentRepository.findByUserIdAndStatus(userId, "ACTIVE");

        double totalInvested = activeInvestments.stream()
                .mapToDouble(Investment::getInvestedAmount).sum();

        double currentValue = activeInvestments.stream()
                .mapToDouble(inv -> inv.getUnits() * inv.getMutualFund().getNav()).sum();

        double gainLoss = currentValue - totalInvested;
        double gainLossPercent = totalInvested > 0 ? (gainLoss / totalInvested) * 100 : 0;

        long activeSips = investmentRepository.findActiveSipsByUserId(userId).size();

        InvestmentDTO.PortfolioSummary summary = new InvestmentDTO.PortfolioSummary();
        summary.setTotalInvested(totalInvested);
        summary.setCurrentValue(currentValue);
        summary.setTotalGainLoss(gainLoss);
        summary.setTotalGainLossPercentage(gainLossPercent);
        summary.setTotalFunds(investmentRepository.countDistinctFundsByUser(userId));
        summary.setActiveSips(activeSips);
        return summary;
    }

    // Admin: get all investments by a specific user
    public List<Investment> getInvestmentsByUser(Long userId) {
        return investmentRepository.findByUserId(userId);
    }

    // Admin: get all investments for a fund
    public List<Investment> getInvestmentsByFund(Long fundId) {
        return investmentRepository.findByMutualFundId(fundId);
    }

    public InvestmentDTO.Response toResponse(Investment inv) {
        InvestmentDTO.Response res = new InvestmentDTO.Response();
        res.setId(inv.getId());
        res.setUserId(inv.getUser().getId());
        res.setUserName(inv.getUser().getFullName());
        res.setMutualFundId(inv.getMutualFund().getId());
        res.setFundName(inv.getMutualFund().getFundName());
        res.setInvestedAmount(inv.getInvestedAmount());
        res.setUnits(inv.getUnits());
        res.setNavAtPurchase(inv.getNavAtPurchase());
        res.setPurchaseDate(inv.getPurchaseDate());
        res.setSip(inv.isSip());
        res.setSipAmount(inv.getSipAmount());
        res.setSipFrequency(inv.getSipFrequency());
        res.setStatus(inv.getStatus());

        double currentNav = inv.getMutualFund().getNav();
        double currentValue = inv.getUnits() * currentNav;
        double gainLoss = currentValue - inv.getInvestedAmount();
        double gainLossPercent = inv.getInvestedAmount() > 0 ? (gainLoss / inv.getInvestedAmount()) * 100 : 0;

        res.setCurrentValue(currentValue);
        res.setGainLoss(gainLoss);
        res.setGainLossPercentage(gainLossPercent);
        return res;
    }
}
