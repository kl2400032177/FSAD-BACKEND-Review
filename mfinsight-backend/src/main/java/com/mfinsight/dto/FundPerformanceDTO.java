package com.mfinsight.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class FundPerformanceDTO {

    @Data
    public static class CreateRequest {
        @NotNull
        private Long mutualFundId;

        @NotNull
        private LocalDate recordDate;

        @NotNull
        private Double nav;

        private Double dailyReturn;
        private Double weeklyReturn;
        private Double monthlyReturn;
    }

    @Data
    public static class Response {
        private Long id;
        private Long mutualFundId;
        private String fundName;
        private LocalDate recordDate;
        private Double nav;
        private Double dailyReturn;
        private Double weeklyReturn;
        private Double monthlyReturn;
    }
}
