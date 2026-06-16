package com.rooftop.garden.controller;

import com.rooftop.garden.entity.ContaminationRecord;
import com.rooftop.garden.service.ContaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contamination")
public class ContaminationController {

    @Autowired
    private ContaminationService contaminationService;

    @GetMapping
    public List<ContaminationRecord> getAllRecords() {
        return contaminationService.getAllRecords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaminationRecord> getRecordById(@PathVariable Long id) {
        return contaminationService.getRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<ContaminationRecord> getRecordsByUserId(@PathVariable Long userId) {
        return contaminationService.getRecordsByUserId(userId);
    }

    @GetMapping("/bin/{binId}")
    public List<ContaminationRecord> getRecordsByBinId(@PathVariable Long binId) {
        return contaminationService.getRecordsByBinId(binId);
    }

    @GetMapping("/batch/{batchId}")
    public List<ContaminationRecord> getRecordsByBatchId(@PathVariable Long batchId) {
        return contaminationService.getRecordsByBatchId(batchId);
    }

    @GetMapping("/status/{status}")
    public List<ContaminationRecord> getRecordsByStatus(@PathVariable ContaminationRecord.ContaminationStatus status) {
        return contaminationService.getRecordsByStatus(status);
    }

    @GetMapping("/pending")
    public List<ContaminationRecord> getPendingRecords() {
        return contaminationService.getPendingRecords();
    }

    @PostMapping("/report")
    public ResponseEntity<?> reportContamination(@RequestBody ContaminationRecord record) {
        try {
            return ResponseEntity.ok(contaminationService.reportContamination(record));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/assign-gardener")
    public ResponseEntity<?> assignToGardener(
            @PathVariable Long id,
            @RequestParam Long gardenerId
    ) {
        try {
            return ResponseEntity.ok(contaminationService.assignToGardener(id, gardenerId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/gardener-dispose")
    public ResponseEntity<?> gardenerDispose(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            Long gardenerId = payload.get("gardenerId") != null ?
                    Long.valueOf(payload.get("gardenerId").toString()) : null;
            ContaminationRecord.DisposalDecision decision = payload.get("decision") != null ?
                    ContaminationRecord.DisposalDecision.valueOf(payload.get("decision").toString()) : null;
            String disposalRemark = payload.get("disposalRemark") != null ?
                    payload.get("disposalRemark").toString() : null;
            Integer extendedDays = payload.get("extendedDays") != null ?
                    Integer.valueOf(payload.get("extendedDays").toString()) : null;
            ContaminationRecord.CompostFinalResult finalResult = payload.get("finalResult") != null ?
                    ContaminationRecord.CompostFinalResult.valueOf(payload.get("finalResult").toString()) : null;

            return ResponseEntity.ok(contaminationService.gardenerDispose(
                    id, gardenerId, decision, disposalRemark, extendedDays, finalResult
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/pause-points")
    public ResponseEntity<?> pauseUserPoints(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            Long propertyApproverId = payload.get("propertyApproverId") != null ?
                    Long.valueOf(payload.get("propertyApproverId").toString()) : null;
            Integer pointsDeducted = payload.get("pointsDeducted") != null ?
                    Integer.valueOf(payload.get("pointsDeducted").toString()) : null;
            String propertyRemark = payload.get("propertyRemark") != null ?
                    payload.get("propertyRemark").toString() : null;
            String recoveryConditions = payload.get("recoveryConditions") != null ?
                    payload.get("recoveryConditions").toString() : null;

            return ResponseEntity.ok(contaminationService.pauseUserPoints(
                    id, propertyApproverId, pointsDeducted, propertyRemark, recoveryConditions
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/recover-points")
    public ResponseEntity<?> recoverUserPoints(
            @PathVariable Long id,
            @RequestParam Long propertyApproverId
    ) {
        try {
            return ResponseEntity.ok(contaminationService.recoverUserPoints(id, propertyApproverId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/update-final-result")
    public ResponseEntity<?> updateFinalResult(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            ContaminationRecord.CompostFinalResult finalResult = payload.get("finalResult") != null ?
                    ContaminationRecord.CompostFinalResult.valueOf(payload.get("finalResult").toString()) : null;
            if (finalResult == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "请指定最终结果"));
            }
            return ResponseEntity.ok(contaminationService.updateFinalResult(id, finalResult));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaminationRecord> updateRecord(
            @PathVariable Long id,
            @RequestBody ContaminationRecord record
    ) {
        return contaminationService.getRecordById(id)
                .map(existing -> {
                    record.setId(id);
                    return ResponseEntity.ok(contaminationService.updateRecord(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
