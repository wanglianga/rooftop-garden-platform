package com.rooftop.garden.service;

import com.rooftop.garden.entity.InspectionRecord;
import com.rooftop.garden.entity.ToolKey;
import com.rooftop.garden.entity.VisitorRecord;
import com.rooftop.garden.entity.RooftopSetting;
import com.rooftop.garden.repository.InspectionRecordRepository;
import com.rooftop.garden.repository.ToolKeyRepository;
import com.rooftop.garden.repository.VisitorRecordRepository;
import com.rooftop.garden.repository.RooftopSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private InspectionRecordRepository inspectionRepository;

    @Autowired
    private ToolKeyRepository toolKeyRepository;

    @Autowired
    private VisitorRecordRepository visitorRepository;

    @Autowired
    private RooftopSettingRepository settingRepository;

    public List<InspectionRecord> getAllInspections() {
        return inspectionRepository.findAll();
    }

    public Optional<InspectionRecord> getInspectionById(Long id) {
        return inspectionRepository.findById(id);
    }

    public List<InspectionRecord> getInspectionsByInspectorId(Long inspectorId) {
        return inspectionRepository.findByInspectorIdOrderByInspectionTimeDesc(inspectorId);
    }

    public List<InspectionRecord> getUnhandledInspections() {
        return inspectionRepository.findByHandledFalse();
    }

    public InspectionRecord createInspection(InspectionRecord inspection) {
        return inspectionRepository.save(inspection);
    }

    public InspectionRecord handleInspection(Long id, String handledBy, String remark) {
        return inspectionRepository.findById(id)
                .map(record -> {
                    record.setHandled(true);
                    record.setHandledBy(handledBy);
                    record.setHandleTime(LocalDateTime.now());
                    if (remark != null) {
                        record.setRemark(remark);
                    }
                    return inspectionRepository.save(record);
                })
                .orElseThrow(() -> new RuntimeException("Inspection record not found"));
    }

    public List<ToolKey> getAllToolKeys() {
        return toolKeyRepository.findAll();
    }

    public Optional<ToolKey> getToolKeyById(Long id) {
        return toolKeyRepository.findById(id);
    }

    public List<ToolKey> getAvailableToolKeys() {
        return toolKeyRepository.findByStatus(ToolKey.KeyStatus.AVAILABLE);
    }

    public ToolKey createToolKey(ToolKey toolKey) {
        if (toolKey.getStatus() == null) {
            toolKey.setStatus(ToolKey.KeyStatus.AVAILABLE);
        }
        return toolKeyRepository.save(toolKey);
    }

    @Transactional
    public ToolKey borrowKey(Long keyId, Long borrowerId, Integer expectedHours) {
        ToolKey key = toolKeyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("Tool key not found"));
        
        if (key.getStatus() != ToolKey.KeyStatus.AVAILABLE) {
            throw new RuntimeException("Tool key is not available");
        }

        key.setStatus(ToolKey.KeyStatus.BORROWED);
        key.setCurrentBorrowerId(borrowerId);
        key.setBorrowTime(LocalDateTime.now());
        key.setExpectedReturnTime(LocalDateTime.now().plusHours(expectedHours != null ? expectedHours : 24));
        
        return toolKeyRepository.save(key);
    }

    @Transactional
    public ToolKey returnKey(Long keyId) {
        ToolKey key = toolKeyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("Tool key not found"));
        
        key.setStatus(ToolKey.KeyStatus.AVAILABLE);
        key.setCurrentBorrowerId(null);
        key.setBorrowTime(null);
        key.setExpectedReturnTime(null);
        
        return toolKeyRepository.save(key);
    }

    public List<VisitorRecord> getAllVisitors() {
        return visitorRepository.findAll();
    }

    public Optional<VisitorRecord> getVisitorById(Long id) {
        return visitorRepository.findById(id);
    }

    public List<VisitorRecord> getVisitingVisitors() {
        return visitorRepository.findByStatus(VisitorRecord.VisitStatus.VISITING);
    }

    public VisitorRecord createVisitor(VisitorRecord visitor) {
        if (visitor.getStatus() == null) {
            visitor.setStatus(VisitorRecord.VisitStatus.PENDING);
        }
        return visitorRepository.save(visitor);
    }

    @Transactional
    public VisitorRecord checkInVisitor(Long id) {
        return visitorRepository.findById(id)
                .map(visitor -> {
                    visitor.setStatus(VisitorRecord.VisitStatus.VISITING);
                    visitor.setEnterTime(LocalDateTime.now());
                    return visitorRepository.save(visitor);
                })
                .orElseThrow(() -> new RuntimeException("Visitor record not found"));
    }

    @Transactional
    public VisitorRecord checkOutVisitor(Long id) {
        return visitorRepository.findById(id)
                .map(visitor -> {
                    visitor.setStatus(VisitorRecord.VisitStatus.LEFT);
                    visitor.setLeaveTime(LocalDateTime.now());
                    return visitorRepository.save(visitor);
                })
                .orElseThrow(() -> new RuntimeException("Visitor record not found"));
    }

    public List<RooftopSetting> getAllSettings() {
        return settingRepository.findAll();
    }

    public Optional<RooftopSetting> getSettingByKey(String key) {
        return settingRepository.findBySettingKey(key);
    }

    public RooftopSetting saveSetting(RooftopSetting setting) {
        return settingRepository.save(setting);
    }
}
