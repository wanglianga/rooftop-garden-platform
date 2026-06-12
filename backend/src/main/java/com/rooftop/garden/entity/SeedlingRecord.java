package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "seedling_records")
public class SeedlingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long plantingId;

    @Column(nullable = false)
    private Long gardenerId;

    @Column(nullable = false)
    private String cropName;

    private Integer seedlingCount;

    private Integer deadCount;

    private Integer replantCount;

    private String reason;

    private String source;

    private String remark;

    private LocalDateTime replantTime;

    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (replantTime == null) {
            replantTime = LocalDateTime.now();
        }
    }
}
