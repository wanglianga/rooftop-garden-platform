package com.rooftop.garden.repository;

import com.rooftop.garden.entity.InspectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Long> {

    List<InspectionRecord> findByInspectorIdOrderByInspectionTimeDesc(Long inspectorId);

    List<InspectionRecord> findByInspectionType(InspectionRecord.InspectionType type);

    List<InspectionRecord> findByHandledFalse();
}
