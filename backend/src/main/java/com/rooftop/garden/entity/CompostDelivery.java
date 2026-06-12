package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "compost_deliveries")
public class CompostDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long binId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal weight;

    private String contaminationLevel;

    private String contaminationDescription;

    private String foodType;

    private Boolean hasPlastic;

    private Boolean hasLiquid;

    private String remark;

    private LocalDateTime deliveryTime;

    private Long collectorId;

    private Boolean collected;

    private LocalDateTime collectTime;

    @PrePersist
    protected void onCreate() {
        if (deliveryTime == null) {
            deliveryTime = LocalDateTime.now();
        }
        if (collected == null) {
            collected = false;
        }
    }
}
