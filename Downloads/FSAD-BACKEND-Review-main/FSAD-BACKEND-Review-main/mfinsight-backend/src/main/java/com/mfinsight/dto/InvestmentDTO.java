package com.mfinsight.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

public class InvestmentDTO {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long mutualFundId;

        @NotNull
        @Positive
        private Double investedAmount;

        @NotNull
        private LocalDate purchaseDate;

        private boolean isSip = false;
        private Double sipAmount;
        private String sipFrequency;  // MONTHLY, WEEKLY
    }

    @Data
    public static class RedeemRequest {
        @NotNull
        private Long investmentId;

        @NotNull
        private LocalDate redemptionDate;
    }

    @Data
    public static class Response {
        private Long id;
        private Long userId;
        private String userName;
        private Long mutualFundId;
        private String fundName;
        private Double investedAmount;
        private Double units;
        private Double navAtPurchase;
        private LocalDate purchaseDate;
        private boolean isSip;
        private Double sipAmount;
        private String sipFrequency;
        private String status;
        private Double currentValue;
        private Double gainLoss;
        private Double gainLossPercentage;
    }

    @Data
    public static class PortfolioSummary {
        private Double totalInvested;
        private Double currentValue;
        private Double totalGainLoss;
        private Double totalGainLossPercentage;
        private Long totalFunds;
        private Long activeSips;
    }
}
