package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tool_keys")
public class ToolKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyCode;

    private String cabinetNumber;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private KeyStatus status;

    private Long currentBorrowerId;

    private LocalDateTime borrowTime;

    private LocalDateTime expectedReturnTime;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum KeyStatus {
        AVAILABLE,
        BORROWED,
        LOST,
        DAMAGED
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
