package com.rooftop.garden.controller;

import com.rooftop.garden.entity.HarvestRecord;
import com.rooftop.garden.service.HarvestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/harvests")
public class HarvestController {

    @Autowired
    private HarvestService harvestService;

    @GetMapping
    public List<HarvestRecord> getAllHarvests() {
        return harvestService.getAllHarvests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HarvestRecord> getHarvestById(@PathVariable Long id) {
        return harvestService.getHarvestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plot/{plotId}")
    public List<HarvestRecord> getHarvestsByPlotId(@PathVariable Long plotId) {
        return harvestService.getHarvestsByPlotId(plotId);
    }

    @GetMapping("/user/{userId}")
    public List<HarvestRecord> getHarvestsByUserId(@PathVariable Long userId) {
        return harvestService.getHarvestsByUserId(userId);
    }

    @GetMapping("/planting/{plantingId}")
    public List<HarvestRecord> getHarvestsByPlantingId(@PathVariable Long plantingId) {
        return harvestService.getHarvestsByPlantingId(plantingId);
    }

    @GetMapping("/distribution/{type}")
    public List<HarvestRecord> getHarvestsByDistributionType(@PathVariable HarvestRecord.DistributionType type) {
        return harvestService.getHarvestsByDistributionType(type);
    }

    @PostMapping
    public HarvestRecord createHarvest(@RequestBody HarvestRecord harvest) {
        return harvestService.createHarvest(harvest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HarvestRecord> updateHarvest(@PathVariable Long id, @RequestBody HarvestRecord harvest) {
        return harvestService.getHarvestById(id)
                .map(existing -> {
                    harvest.setId(id);
                    return ResponseEntity.ok(harvestService.updateHarvest(harvest));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHarvest(@PathVariable Long id) {
        if (harvestService.getHarvestById(id).isPresent()) {
            harvestService.deleteHarvest(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
