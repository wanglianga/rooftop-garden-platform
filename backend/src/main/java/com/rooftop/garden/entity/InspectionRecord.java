package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inspection_records")
public class InspectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long inspectorId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InspectionType inspectionType;

    private String location;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InspectionResult result;

    private String issueDescription;

    private String handlingSuggestion;

    private Boolean handled;

    private String handledBy;

    private LocalDateTime handleTime;

    private String images;

    private String remark;

    private LocalDateTime inspectionTime;

    private LocalDateTime createTime;

    public enum InspectionType {
        WATERPROOF,
        SAFETY,
        TOOL_CABINET,
        GENERAL
    }

    public enum InspectionResult {
        NORMAL,
        ABNORMAL,
        NEED_REPAIR
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (inspectionTime == null) {
            inspectionTime = LocalDateTime.now();
        }
        if (handled == null) {
            handled = false;
        }
    }
}
