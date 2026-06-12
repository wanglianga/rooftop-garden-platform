package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "plots")
public class Plot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String plotCode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal area;

    @Column(nullable = false)
    private Integer positionX;

    @Column(nullable = false)
    private Integer positionY;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlotStatus status;

    private String sunlightPeriod;

    private String soilType;

    private String soilPH;

    private String irrigationType;

    private Long currentClaimId;

    private String currentCrop;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum PlotStatus {
        AVAILABLE,
        CLAIMED,
        PLANTING,
        HARVESTING,
        MAINTENANCE,
        IDLE
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
