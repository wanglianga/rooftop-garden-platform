package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "visitor_records")
public class VisitorRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String visitorName;

    private String visitorPhone;

    private String idCard;

    private String visitPurpose;

    private Long invitedById;

    private String plotNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VisitStatus status;

    private LocalDateTime enterTime;

    private LocalDateTime leaveTime;

    private String remark;

    private LocalDateTime createTime;

    public enum VisitStatus {
        PENDING,
        VISITING,
        LEFT,
        CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
