package com.rooftop.garden.controller;

import com.rooftop.garden.entity.CareLog;
import com.rooftop.garden.service.CareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/care-logs")
public class CareLogController {

    @Autowired
    private CareService careService;

    @GetMapping
    public List<CareLog> getAllCareLogs() {
        return careService.getAllCareLogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareLog> getCareLogById(@PathVariable Long id) {
        return careService.getCareLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plot/{plotId}")
    public List<CareLog> getCareLogsByPlotId(@PathVariable Long plotId) {
        return careService.getCareLogsByPlotId(plotId);
    }

    @GetMapping("/planting/{plantingId}")
    public List<CareLog> getCareLogsByPlantingId(@PathVariable Long plantingId) {
        return careService.getCareLogsByPlantingId(plantingId);
    }

    @GetMapping("/user/{userId}")
    public List<CareLog> getCareLogsByUserId(@PathVariable Long userId) {
        return careService.getCareLogsByUserId(userId);
    }

    @GetMapping("/plot/{plotId}/type/{type}")
    public List<CareLog> getCareLogsByPlotIdAndType(@PathVariable Long plotId, @PathVariable CareLog.CareType type) {
        return careService.getCareLogsByPlotIdAndType(plotId, type);
    }

    @PostMapping
    public CareLog createCareLog(@RequestBody CareLog careLog) {
        return careService.createCareLog(careLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareLog> updateCareLog(@PathVariable Long id, @RequestBody CareLog careLog) {
        return careService.getCareLogById(id)
                .map(existing -> {
                    careLog.setId(id);
                    return ResponseEntity.ok(careService.updateCareLog(careLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareLog(@PathVariable Long id) {
        if (careService.getCareLogById(id).isPresent()) {
            careService.deleteCareLog(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
