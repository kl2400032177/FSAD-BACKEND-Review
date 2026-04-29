package com.mfinsight.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fund_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mutual_fund_id", nullable = false)
    private MutualFund mutualFund;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private Double nav;

    private Double dailyReturn;     // % return for that day
    private Double weeklyReturn;
    private Double monthlyReturn;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
