package com.rooftop.garden.controller;

import com.rooftop.garden.entity.PlantingRecord;
import com.rooftop.garden.service.PlantingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plantings")
public class PlantingController {

    @Autowired
    private PlantingService plantingService;

    @GetMapping
    public List<PlantingRecord> getAllPlantings() {
        return plantingService.getAllPlantings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantingRecord> getPlantingById(@PathVariable Long id) {
        return plantingService.getPlantingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plot/{plotId}")
    public List<PlantingRecord> getPlantingsByPlotId(@PathVariable Long plotId) {
        return plantingService.getPlantingsByPlotId(plotId);
    }

    @GetMapping("/user/{userId}")
    public List<PlantingRecord> getPlantingsByUserId(@PathVariable Long userId) {
        return plantingService.getPlantingsByUserId(userId);
    }

    @GetMapping("/status/{status}")
    public List<PlantingRecord> getPlantingsByStatus(@PathVariable PlantingRecord.PlantingStatus status) {
        return plantingService.getPlantingsByStatus(status);
    }

    @PostMapping
    public PlantingRecord createPlanting(@RequestBody PlantingRecord planting) {
        return plantingService.createPlanting(planting);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantingRecord> updatePlanting(@PathVariable Long id, @RequestBody PlantingRecord planting) {
        return plantingService.getPlantingById(id)
                .map(existing -> {
                    planting.setId(id);
                    return ResponseEntity.ok(plantingService.updatePlanting(planting));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PlantingRecord> updateStatus(@PathVariable Long id, @RequestParam PlantingRecord.PlantingStatus status) {
        try {
            return ResponseEntity.ok(plantingService.updateStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<PlantingRecord> completePlanting(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(plantingService.completePlanting(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
