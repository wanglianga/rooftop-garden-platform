package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "compost_batches")
public class CompostBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String batchCode;

    private Long sourceBinId;

    @Column(nullable = false)
    private BigDecimal initialWeight;

    private BigDecimal finalWeight;

    private String materials;

    private LocalDate startDate;

    private LocalDate expectedMaturityDate;

    private LocalDate maturityDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    private Integer turnCount;

    private LocalDate lastTurnDate;

    private String temperatureRecord;

    private String moistureLevel;

    private String qualityGrade;

    private String matureDestination;

    private Long usedPlotId;

    private String remark;

    private Boolean contaminated;

    private Long contaminationRecordId;

    private String contaminationRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum BatchStatus {
        COLLECTING,
        FERMENTING,
        MATURING,
        MATURE,
        USED,
        SOLD,
        ISOLATED,
        SCRAPPED,
        EXTENDED_FERMENTATION
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (turnCount == null) {
            turnCount = 0;
        }
        if (contaminated == null) {
            contaminated = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
