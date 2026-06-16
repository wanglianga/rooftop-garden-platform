package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contamination_records")
public class ContaminationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long binId;

    @Column(nullable = false)
    private Long deliveryId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long reporterId;

    private String photoUrl;

    @Column(nullable = false)
    private String contaminationTypes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeverityLevel severityLevel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContaminationStatus status;

    private Long batchId;

    private Long gardenerId;

    @Enumerated(EnumType.STRING)
    private DisposalDecision disposalDecision;

    private String disposalRemark;

    private Integer extendedDays;

    private Long propertyApproverId;

    private String propertyRemark;

    private Integer pointsDeducted;

    @Enumerated(EnumType.STRING)
    private CompostFinalResult compostFinalResult;

    private String recoveryConditions;

    private LocalDateTime reportTime;

    private LocalDateTime disposalTime;

    private LocalDateTime recoveryTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum SeverityLevel {
        MILD,
        MODERATE,
        SEVERE
    }

    public enum ContaminationStatus {
        REPORTED,
        ISOLATED,
        PENDING_DISPOSAL,
        DISPOSED,
        POINTS_PAUSED,
        RECOVERED,
        CANCELLED
    }

    public enum DisposalDecision {
        SCRAP_WHOLE,
        PARTIAL_PICK,
        EXTEND_FERMENTATION,
        NORMAL_USE
    }

    public enum CompostFinalResult {
        BACKFILL_AVAILABLE,
        BACKFILL_UNAVAILABLE,
        PARTIALLY_BACKFILL,
        PENDING
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (reportTime == null) {
            reportTime = LocalDateTime.now();
        }
        if (status == null) {
            status = ContaminationStatus.REPORTED;
        }
        if (compostFinalResult == null) {
            compostFinalResult = CompostFinalResult.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
