package com.mfinsight.dto;

import com.mfinsight.enums.FundCategory;
import com.mfinsight.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class MutualFundDTO {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String fundName;

        @NotBlank
        private String amcName;

        @NotNull
        private FundCategory category;

        @NotNull
        private RiskLevel riskLevel;

        @NotNull
        private Double nav;

        private Double expenseRatio;
        private Double oneYearReturn;
        private Double threeYearReturn;
        private Double fiveYearReturn;
        private Double aum;
        private Double minimumInvestment;
        private Double minimumSip;
        private String description;

        @NotNull
        private LocalDate inceptionDate;
    }

    @Data
    public static class UpdateRequest {
        private String fundName;
        private FundCategory category;
        private RiskLevel riskLevel;
        private Double nav;
        private Double expenseRatio;
        private Double oneYearReturn;
        private Double threeYearReturn;
        private Double fiveYearReturn;
        private Double aum;
        private Double minimumInvestment;
        private Double minimumSip;
        private String description;
        private Boolean active;
    }

    @Data
    public static class Response {
        private Long id;
        private String fundName;
        private String amcName;
        private FundCategory category;
        private RiskLevel riskLevel;
        private Double nav;
        private Double expenseRatio;
        private Double oneYearReturn;
        private Double threeYearReturn;
        private Double fiveYearReturn;
        private Double aum;
        private Double minimumInvestment;
        private Double minimumSip;
        private String description;
        private LocalDate inceptionDate;
        private boolean active;
    }
}
