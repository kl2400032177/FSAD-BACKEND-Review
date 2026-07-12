package com.mfinsight.entity;

import com.mfinsight.enums.FundCategory;
import com.mfinsight.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mutual_funds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MutualFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String fundName;

    @NotBlank
    @Column(nullable = false)
    private String amcName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private Double nav;

    private Double expenseRatio;

    private Double oneYearReturn;
    private Double threeYearReturn;
    private Double fiveYearReturn;

    private Double aum;

    private Double minimumInvestment;
    private Double minimumSip;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDate inceptionDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "mutualFund", cascade = CascadeType.ALL)
    private List<FundPerformance> performanceHistory;

    @OneToMany(mappedBy = "mutualFund", cascade = CascadeType.ALL)
    private List<Investment> investments;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}