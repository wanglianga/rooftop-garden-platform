package com.rooftop.garden.service;

import com.rooftop.garden.entity.*;
import com.rooftop.garden.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContaminationService {

    @Autowired
    private ContaminationRecordRepository contaminationRepository;

    @Autowired
    private CompostBinRepository binRepository;

    @Autowired
    private CompostBatchRepository batchRepository;

    @Autowired
    private CompostDeliveryRepository deliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompostBatchService batchService;

    public List<ContaminationRecord> getAllRecords() {
        return contaminationRepository.findAll();
    }

    public Optional<ContaminationRecord> getRecordById(Long id) {
        return contaminationRepository.findById(id);
    }

    public List<ContaminationRecord> getRecordsByUserId(Long userId) {
        return contaminationRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<ContaminationRecord> getRecordsByBinId(Long binId) {
        return contaminationRepository.findByBinId(binId);
    }

    public List<ContaminationRecord> getRecordsByBatchId(Long batchId) {
        return contaminationRepository.findByBatchId(batchId);
    }

    public List<ContaminationRecord> getRecordsByStatus(ContaminationRecord.ContaminationStatus status) {
        return contaminationRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public List<ContaminationRecord> getPendingRecords() {
        return contaminationRepository.findByStatusInOrderByCreateTimeDesc(
            List.of(
                ContaminationRecord.ContaminationStatus.REPORTED,
                ContaminationRecord.ContaminationStatus.ISOLATED,
                ContaminationRecord.ContaminationStatus.PENDING_DISPOSAL,
                ContaminationRecord.ContaminationStatus.POINTS_PAUSED
            )
        );
    }

    @Transactional
    public ContaminationRecord reportContamination(ContaminationRecord record) {
        if (record.getDeliveryId() == null) {
            throw new IllegalArgumentException("请关联投放记录");
        }
        if (record.getBinId() == null) {
            throw new IllegalArgumentException("请关联厨余桶");
        }
        if (record.getUserId() == null) {
            throw new IllegalArgumentException("请关联住户信息");
        }
        if (record.getReporterId() == null) {
            throw new IllegalArgumentException("请登记回收员信息");
        }
        if (record.getContaminationTypes() == null || record.getContaminationTypes().trim().isEmpty()) {
            throw new IllegalArgumentException("请标记污染类型");
        }
        if (record.getSeverityLevel() == null) {
            throw new IllegalArgumentException("请评估污染程度");
        }

        CompostDelivery delivery = deliveryRepository.findById(record.getDeliveryId())
                .orElseThrow(() -> new RuntimeException("投放记录不存在"));

        CompostBin bin = binRepository.findById(record.getBinId())
                .orElseThrow(() -> new RuntimeException("厨余桶不存在"));

        record.setStatus(ContaminationRecord.ContaminationStatus.ISOLATED);
        ContaminationRecord saved = contaminationRepository.save(record);

        bin.setContaminated(true);
        bin.setContaminationRecordId(saved.getId());
        bin.setStatus(CompostBin.BinStatus.ISOLATED);
        binRepository.save(bin);

        List<CompostBatch> relatedBatches = batchRepository.findBySourceBinId(bin.getId())
                .stream()
                .filter(b -> b.getStatus() == CompostBatch.BatchStatus.COLLECTING ||
                        b.getStatus() == CompostBatch.BatchStatus.FERMENTING)
                .toList();

        if (!relatedBatches.isEmpty()) {
            CompostBatch batch = relatedBatches.get(0);
            batch.setContaminated(true);
            batch.setContaminationRecordId(saved.getId());
            batch.setStatus(CompostBatch.BatchStatus.ISOLATED);
            batch.setContaminationRemark("因住户投放污染被隔离，等待园艺师判定处理方式");
            batchRepository.save(batch);
            saved.setBatchId(batch.getId());
            saved = contaminationRepository.save(saved);
        }

        return saved;
    }

    @Transactional
    public ContaminationRecord assignToGardener(Long recordId, Long gardenerId) {
        ContaminationRecord record = contaminationRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("污染记录不存在"));

        if (record.getStatus() != ContaminationRecord.ContaminationStatus.ISOLATED) {
            throw new RuntimeException("当前状态不可分配园艺师");
        }

        User gardener = userRepository.findById(gardenerId)
                .orElseThrow(() -> new RuntimeException("园艺师不存在"));

        if (gardener.getRole() != User.Role.GARDENER) {
            throw new IllegalArgumentException("该用户不是园艺师");
        }

        record.setGardenerId(gardenerId);
        record.setStatus(ContaminationRecord.ContaminationStatus.PENDING_DISPOSAL);

        return contaminationRepository.save(record);
    }

    @Transactional
    public ContaminationRecord gardenerDispose(
            Long recordId,
            Long gardenerId,
            ContaminationRecord.DisposalDecision decision,
            String disposalRemark,
            Integer extendedDays,
            ContaminationRecord.CompostFinalResult finalResult
    ) {
        ContaminationRecord record = contaminationRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("污染记录不存在"));

        if (record.getStatus() != ContaminationRecord.ContaminationStatus.PENDING_DISPOSAL) {
            throw new RuntimeException("当前状态不可进行处理判定");
        }

        if (record.getGardenerId() != null && !record.getGardenerId().equals(gardenerId)) {
            throw new RuntimeException("该记录已分配给其他园艺师处理");
        }

        if (decision == null) {
            throw new IllegalArgumentException("请选择处理方式");
        }

        record.setGardenerId(gardenerId);
        record.setDisposalDecision(decision);
        record.setDisposalRemark(disposalRemark);
        record.setExtendedDays(extendedDays);
        record.setCompostFinalResult(finalResult != null ? finalResult : ContaminationRecord.CompostFinalResult.PENDING);
        record.setDisposalTime(LocalDateTime.now());
        record.setStatus(ContaminationRecord.ContaminationStatus.DISPOSED);

        if (record.getBatchId() != null) {
            CompostBatch batch = batchRepository.findById(record.getBatchId()).orElse(null);
            if (batch != null) {
                switch (decision) {
                    case SCRAP_WHOLE:
                        batch.setStatus(CompostBatch.BatchStatus.SCRAPPED);
                        batch.setContaminationRemark("园艺师判定整桶报废：" + (disposalRemark != null ? disposalRemark : ""));
                        if (finalResult == null) {
                            record.setCompostFinalResult(ContaminationRecord.CompostFinalResult.BACKFILL_UNAVAILABLE);
                        }
                        break;
                    case PARTIAL_PICK:
                        batch.setStatus(CompostBatch.BatchStatus.FERMENTING);
                        batch.setContaminationRemark("已局部挑拣污染部分，继续正常发酵：" + (disposalRemark != null ? disposalRemark : ""));
                        if (finalResult == null) {
                            record.setCompostFinalResult(ContaminationRecord.CompostFinalResult.PARTIALLY_BACKFILL);
                        }
                        break;
                    case EXTEND_FERMENTATION:
                        batch.setStatus(CompostBatch.BatchStatus.EXTENDED_FERMENTATION);
                        if (extendedDays != null && extendedDays > 0) {
                            LocalDate newExpected = (batch.getExpectedMaturityDate() != null ?
                                    batch.getExpectedMaturityDate() : LocalDate.now()).plusDays(extendedDays);
                            batch.setExpectedMaturityDate(newExpected);
                        }
                        batch.setContaminationRemark("延长发酵期" + (extendedDays != null ? extendedDays : 0) +
                                "天：" + (disposalRemark != null ? disposalRemark : ""));
                        if (finalResult == null) {
                            record.setCompostFinalResult(ContaminationRecord.CompostFinalResult.BACKFILL_AVAILABLE);
                        }
                        break;
                    case NORMAL_USE:
                        batch.setStatus(CompostBatch.BatchStatus.FERMENTING);
                        batch.setContaminated(false);
                        batch.setContaminationRemark("经园艺师确认无污染，恢复正常发酵流程");
                        if (finalResult == null) {
                            record.setCompostFinalResult(ContaminationRecord.CompostFinalResult.BACKFILL_AVAILABLE);
                        }
                        break;
                }
                batchRepository.save(batch);
            }
        }

        if (record.getBinId() != null) {
            CompostBin bin = binRepository.findById(record.getBinId()).orElse(null);
            if (bin != null) {
                switch (decision) {
                    case SCRAP_WHOLE:
                        bin.setCurrentWeight(BigDecimal.ZERO);
                        bin.setStatus(CompostBin.BinStatus.MAINTENANCE);
                        bin.setContaminated(false);
                        bin.setContaminationRecordId(null);
                        binRepository.save(bin);
                        break;
                    case NORMAL_USE:
                        bin.setContaminated(false);
                        bin.setContaminationRecordId(null);
                        if (bin.getCurrentWeight().compareTo(bin.getCapacity()) >= 0) {
                            bin.setStatus(CompostBin.BinStatus.FULL);
                        } else if (bin.getCurrentWeight().compareTo(BigDecimal.ZERO) > 0) {
                            bin.setStatus(CompostBin.BinStatus.FILLING);
                        } else {
                            bin.setStatus(CompostBin.BinStatus.ACTIVE);
                        }
                        binRepository.save(bin);
                        break;
                }
            }
        }

        return contaminationRepository.save(record);
    }

    @Transactional
    public ContaminationRecord pauseUserPoints(
            Long recordId,
            Long propertyApproverId,
            Integer pointsDeducted,
            String propertyRemark,
            String recoveryConditions
    ) {
        ContaminationRecord record = contaminationRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("污染记录不存在"));

        if (record.getStatus() != ContaminationRecord.ContaminationStatus.DISPOSED) {
            throw new RuntimeException("需等待园艺师处理完成后再执行积分暂停");
        }

        User approver = userRepository.findById(propertyApproverId)
                .orElseThrow(() -> new RuntimeException("物业人员不存在"));

        if (approver.getRole() != User.Role.PROPERTY) {
            throw new IllegalArgumentException("该用户没有物业权限");
        }

        User resident = userRepository.findById(record.getUserId())
                .orElseThrow(() -> new RuntimeException("住户不存在"));

        record.setPropertyApproverId(propertyApproverId);
        record.setPropertyRemark(propertyRemark);
        record.setPointsDeducted(pointsDeducted != null ? pointsDeducted : 0);
        record.setRecoveryConditions(recoveryConditions);

        if (pointsDeducted != null && pointsDeducted > 0) {
            int currentPoints = resident.getGreenPoints() != null ? resident.getGreenPoints() : 0;
            resident.setGreenPoints(Math.max(0, currentPoints - pointsDeducted));
        }

        resident.setPointsPaused(true);
        resident.setPointsPauseStartTime(LocalDateTime.now());
        resident.setPointsPauseReason("因厨余污染被暂停积分增长：" +
                (record.getContaminationTypes() != null ? record.getContaminationTypes() : ""));
        userRepository.save(resident);

        record.setStatus(ContaminationRecord.ContaminationStatus.POINTS_PAUSED);

        return contaminationRepository.save(record);
    }

    @Transactional
    public ContaminationRecord recoverUserPoints(Long recordId, Long propertyApproverId) {
        ContaminationRecord record = contaminationRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("污染记录不存在"));

        if (record.getStatus() != ContaminationRecord.ContaminationStatus.POINTS_PAUSED) {
            throw new RuntimeException("当前状态不可恢复积分");
        }

        User approver = userRepository.findById(propertyApproverId)
                .orElseThrow(() -> new RuntimeException("物业人员不存在"));

        if (approver.getRole() != User.Role.PROPERTY) {
            throw new IllegalArgumentException("该用户没有物业权限");
        }

        User resident = userRepository.findById(record.getUserId())
                .orElseThrow(() -> new RuntimeException("住户不存在"));

        resident.setPointsPaused(false);
        resident.setPointsPauseStartTime(null);
        resident.setPointsPauseReason(null);
        userRepository.save(resident);

        record.setStatus(ContaminationRecord.ContaminationStatus.RECOVERED);
        record.setRecoveryTime(LocalDateTime.now());

        return contaminationRepository.save(record);
    }

    @Transactional
    public ContaminationRecord updateFinalResult(
            Long recordId,
            ContaminationRecord.CompostFinalResult finalResult
    ) {
        ContaminationRecord record = contaminationRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("污染记录不存在"));

        record.setCompostFinalResult(finalResult);

        return contaminationRepository.save(record);
    }

    public ContaminationRecord updateRecord(ContaminationRecord record) {
        return contaminationRepository.save(record);
    }
}
