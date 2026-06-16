package com.rooftop.garden.repository;

import com.rooftop.garden.entity.ContaminationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaminationRecordRepository extends JpaRepository<ContaminationRecord, Long> {

    List<ContaminationRecord> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<ContaminationRecord> findByBinId(Long binId);

    List<ContaminationRecord> findByBatchId(Long batchId);

    List<ContaminationRecord> findByDeliveryId(Long deliveryId);

    List<ContaminationRecord> findByStatusOrderByCreateTimeDesc(ContaminationRecord.ContaminationStatus status);

    List<ContaminationRecord> findByReporterId(Long reporterId);

    List<ContaminationRecord> findByGardenerId(Long gardenerId);

    List<ContaminationRecord> findByStatusInOrderByCreateTimeDesc(List<ContaminationRecord.ContaminationStatus> statuses);
}
